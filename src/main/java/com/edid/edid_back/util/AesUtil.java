package com.audigo.audigo_back.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * AES-256-CBC 암호화/복호화 유틸리티
 * Node.js의 EncryptAES/DecryptAES 기능 구현
 */
@Component
@Slf4j
public class AesUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final int IV_LENGTH = 16;

    @Value("${aes.secret.key}")
    private String aesSecretKey;

    @Value("${aes.member.secret.key}")
    private String aesMemberSecretKey;

    @Value("${aes.salt}")
    private String aesSalt;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Admin용 암호화 키 생성 (PBKDF2 사용)
     */
    private SecretKey getAdminKey() throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(
                aesSecretKey.toCharArray(),
                aesSalt.getBytes(StandardCharsets.UTF_8),
                1213,
                256
        );
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    /**
     * Member용 암호화 키 생성 (직접 사용)
     */
    private SecretKey getMemberKey() {
        byte[] keyBytes = aesMemberSecretKey.getBytes(StandardCharsets.UTF_8);
        // 32바이트로 패딩 또는 자르기
        byte[] key32 = new byte[32];
        System.arraycopy(keyBytes, 0, key32, 0, Math.min(keyBytes.length, 32));
        return new SecretKeySpec(key32, KEY_ALGORITHM);
    }

    /**
     * 암호화 (Admin 또는 Member용)
     * @param data 암호화할 데이터 (Object)
     * @param isAdmin true=Admin용, false=Member용
     * @return URL-safe Base64로 인코딩된 iv+암호문
     */
    public String encrypt(Object data, boolean isAdmin) {
        try {
            // 1. JSON으로 변환
            String jsonData = objectMapper.writeValueAsString(data);

            // 2. 키 선택
            SecretKey secretKey = isAdmin ? getAdminKey() : getMemberKey();

            // 3. 랜덤 IV 생성
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 4. 암호화
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(jsonData.getBytes(StandardCharsets.UTF_8));

            // 5. IV + 암호문 결합
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            // 6. URL-safe Base64로 인코딩
            String base64 = Base64.getEncoder().encodeToString(combined);
            return base64
                    .replace("+", "-")
                    .replace("/", "_")
                    .replace("=", "");

        } catch (Exception e) {
            log.error("Encryption error", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * 복호화 (Admin 또는 Member용)
     * @param encrypted URL-safe Base64로 인코딩된 iv+암호문
     * @param isAdmin true=Admin용, false=Member용
     * @param clazz 복호화 결과 타입
     * @return 복호화된 객체
     */
    public <T> T decrypt(String encrypted, boolean isAdmin, Class<T> clazz) {
        try {
            // 1. URL-safe Base64 디코딩
            String base64 = encrypted
                    .replace("-", "+")
                    .replace("_", "/");

            // 패딩 추가
            int paddingLength = (4 - (base64.length() % 4)) % 4;
            base64 = base64 + "=".repeat(paddingLength);

            byte[] combined = Base64.getDecoder().decode(base64);

            // 2. IV와 암호문 분리
            byte[] iv = new byte[IV_LENGTH];
            byte[] encryptedText = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, encryptedText, 0, encryptedText.length);

            // 3. 키 선택
            SecretKey secretKey = isAdmin ? getAdminKey() : getMemberKey();

            // 4. 복호화
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] decrypted = cipher.doFinal(encryptedText);

            // 5. JSON 파싱
            String jsonData = new String(decrypted, StandardCharsets.UTF_8);
            return objectMapper.readValue(jsonData, clazz);

        } catch (Exception e) {
            log.error("Decryption error", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * Admin용 암호화 (간편 메서드)
     */
    public String encryptAdmin(Object data) {
        return encrypt(data, true);
    }

    /**
     * Member용 암호화 (간편 메서드)
     */
    public String encryptMember(Object data) {
        return encrypt(data, false);
    }

    /**
     * Admin용 복호화 (간편 메서드)
     */
    public <T> T decryptAdmin(String encrypted, Class<T> clazz) {
        return decrypt(encrypted, true, clazz);
    }

    /**
     * Member용 복호화 (간편 메서드)
     */
    public <T> T decryptMember(String encrypted, Class<T> clazz) {
        return decrypt(encrypted, false, clazz);
    }
}
