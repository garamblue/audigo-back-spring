package com.audigo.audigo_back.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * AWS KMS 기반 Ethereum/BSC 트랜잭션 서명 유틸리티
 * Web3j와 AWS KMS를 연동하여 안전하게 트랜잭션에 서명
 */
@Component
@Slf4j
public class KmsSignerUtil {

    @Value("${aws.kms.region}")
    private String kmsRegion;

    @Value("${aws.kms.access.key.id}")
    private String kmsAccessKeyId;

    @Value("${aws.kms.secret.access.key}")
    private String kmsSecretAccessKey;

    @Value("${aws.kms.eoa.key.id}")
    private String kmsEoaKeyId;

    private KmsClient kmsClient;
    private String cachedAddress;

    // secp256k1 curve order
    private static final BigInteger N = new BigInteger(
            "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
    private static final BigInteger HALF_N = N.shiftRight(1);

    @PostConstruct
    public void init() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                kmsAccessKeyId,
                kmsSecretAccessKey
        );

        this.kmsClient = KmsClient.builder()
                .region(Region.of(kmsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        log.info("KMS Signer initialized with region: {}", kmsRegion);
    }

    /**
     * KMS 공개키로부터 Ethereum 주소 추출
     */
    public String getAddress() {
        if (cachedAddress != null) {
            return cachedAddress;
        }

        try {
            GetPublicKeyRequest request = GetPublicKeyRequest.builder()
                    .keyId(kmsEoaKeyId)
                    .build();

            GetPublicKeyResponse response = kmsClient.getPublicKey(request);
            byte[] publicKeyDer = response.publicKey().asByteArray();

            // SPKI DER에서 uncompressed public key 추출 (0x04 + 64 bytes)
            byte[] uncompressedPubKey = extractUncompressedPublicKey(publicKeyDer);

            // Ethereum 주소 계산: keccak256(pubkey_xy) 마지막 20바이트
            byte[] pubKeyXY = Arrays.copyOfRange(uncompressedPubKey, 1, 65); // 0x04 제거
            byte[] hash = sha3(pubKeyXY);
            byte[] addressBytes = Arrays.copyOfRange(hash, 12, 32); // 마지막 20바이트

            cachedAddress = "0x" + Numeric.toHexStringNoPrefix(addressBytes);
            log.info("KMS Ethereum address: {}", cachedAddress);

            return cachedAddress;

        } catch (Exception e) {
            log.error("Failed to get KMS address", e);
            throw new RuntimeException("Failed to get KMS address", e);
        }
    }

    /**
     * 트랜잭션 해시에 서명
     * @param transactionHash 트랜잭션 해시 (32 bytes)
     * @return Sign.SignatureData
     */
    public Sign.SignatureData signTransactionHash(byte[] transactionHash) {
        try {
            // KMS로 서명 요청
            SignRequest signRequest = SignRequest.builder()
                    .keyId(kmsEoaKeyId)
                    .message(SdkBytes.fromByteArray(transactionHash))
                    .messageType(MessageType.DIGEST)
                    .signingAlgorithm(SigningAlgorithmSpec.ECDSA_SHA_256)
                    .build();

            SignResponse signResponse = kmsClient.sign(signRequest);
            byte[] signature = signResponse.signature().asByteArray();

            // DER 포맷에서 R, S 추출 및 정규화
            RsSignature rsSignature = parseDerSignature(signature);

            // Recovery ID (v) 찾기
            int recId = findRecoveryId(transactionHash, rsSignature);

            // Ethereum 서명 형식으로 변환
            byte[] r = Numeric.toBytesPadded(rsSignature.r, 32);
            byte[] s = Numeric.toBytesPadded(rsSignature.s, 32);
            byte v = (byte) (recId + 27); // Ethereum uses 27 or 28

            log.debug("Transaction signed with v={}, r={}, s={}",
                    v, Numeric.toHexString(r), Numeric.toHexString(s));

            return new Sign.SignatureData(v, r, s);

        } catch (Exception e) {
            log.error("Failed to sign transaction with KMS", e);
            throw new RuntimeException("KMS signing failed", e);
        }
    }

    /**
     * DER 포맷 서명에서 R, S 추출 및 정규화
     */
    private RsSignature parseDerSignature(byte[] derSignature) {
        int offset = 2; // SEQUENCE 헤더 skip

        // R 값 읽기
        if (derSignature[offset] != 0x02) {
            throw new RuntimeException("Invalid DER: No Integer tag for R");
        }
        offset++;
        int rLength = derSignature[offset++];
        byte[] rBytes = Arrays.copyOfRange(derSignature, offset, offset + rLength);
        offset += rLength;

        // S 값 읽기
        if (derSignature[offset] != 0x02) {
            throw new RuntimeException("Invalid DER: No Integer tag for S");
        }
        offset++;
        int sLength = derSignature[offset++];
        byte[] sBytes = Arrays.copyOfRange(derSignature, offset, offset + sLength);

        // DER sign bit 회피용 0x00 제거
        if (rBytes[0] == 0x00) {
            rBytes = Arrays.copyOfRange(rBytes, 1, rBytes.length);
        }
        if (sBytes[0] == 0x00) {
            sBytes = Arrays.copyOfRange(sBytes, 1, sBytes.length);
        }

        BigInteger r = new BigInteger(1, rBytes);
        BigInteger s = new BigInteger(1, sBytes);

        // s를 low-s로 정규화 (Ethereum 요구사항)
        if (s.compareTo(HALF_N) > 0) {
            s = N.subtract(s);
            log.debug("Normalized S to low-s form");
        }

        return new RsSignature(r, s);
    }

    /**
     * Recovery ID (v) 찾기
     */
    private int findRecoveryId(byte[] messageHash, RsSignature signature) {
        String myAddress = getAddress().toLowerCase();

        // v=0, 1 시도
        for (int recId = 0; recId < 2; recId++) {
            try {
                byte[] r = Numeric.toBytesPadded(signature.r, 32);
                byte[] s = Numeric.toBytesPadded(signature.s, 32);
                byte v = (byte) recId;

                Sign.SignatureData sig = new Sign.SignatureData(v, r, s);
                BigInteger recoveredPubKey = Sign.signedMessageHashToKey(messageHash, sig);

                String recoveredAddress = "0x" + Keys.getAddress(recoveredPubKey);

                log.debug("Recovery attempt recId={}, recovered={}", recId, recoveredAddress);

                if (recoveredAddress.equalsIgnoreCase(myAddress)) {
                    log.debug("Match found with recId={}", recId);
                    return recId;
                }
            } catch (Exception e) {
                log.debug("Recovery failed for recId={}: {}", recId, e.getMessage());
            }
        }

        throw new RuntimeException("Could not find correct recovery ID (v) for KMS signature");
    }

    /**
     * SPKI DER에서 uncompressed public key 추출
     */
    private byte[] extractUncompressedPublicKey(byte[] spkiDer) {
        // BIT STRING (0x03) 태그 찾기
        for (int i = 0; i < spkiDer.length - 66; i++) {
            if (spkiDer[i] == 0x03 && spkiDer[i + 1] == 0x42 &&
                    spkiDer[i + 2] == 0x00 && spkiDer[i + 3] == 0x04) {
                byte[] pubKey = Arrays.copyOfRange(spkiDer, i + 3, i + 3 + 65);
                if (pubKey.length == 65 && pubKey[0] == 0x04) {
                    return pubKey;
                }
            }
        }

        // fallback: 끝에서 65바이트
        byte[] tail = Arrays.copyOfRange(spkiDer, spkiDer.length - 65, spkiDer.length);
        if (tail.length == 65 && tail[0] == 0x04) {
            return tail;
        }

        throw new RuntimeException("Cannot extract uncompressed secp256k1 public key from SPKI");
    }

    /**
     * Keccak-256 해시 (Ethereum의 SHA3)
     */
    private byte[] sha3(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("KECCAK-256", "BC");
            return digest.digest(input);
        } catch (Exception e) {
            // Fallback to Web3j's implementation
            return org.web3j.crypto.Hash.sha3(input);
        }
    }

    /**
     * R, S 서명 데이터 클래스
     */
    private static class RsSignature {
        final BigInteger r;
        final BigInteger s;

        RsSignature(BigInteger r, BigInteger s) {
            this.r = r;
            this.s = s;
        }
    }

    /**
     * KMS Client 반환 (직접 사용이 필요한 경우)
     */
    public KmsClient getKmsClient() {
        return kmsClient;
    }
}
