package com.audigo.audigo_back.util;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * Web3 유틸리티 클래스
 */
public class Web3Util {

    /**
     * Wei를 Ether(또는 Token)로 변환
     */
    public static BigDecimal weiToEther(BigInteger wei) {
        return Convert.fromWei(new BigDecimal(wei), Convert.Unit.ETHER);
    }

    /**
     * Wei를 Gwei로 변환
     */
    public static BigDecimal weiToGwei(BigInteger wei) {
        return new BigDecimal(wei).divide(new BigDecimal("1000000000000000000"));
    }

    /**
     * Ether(또는 Token)를 Wei로 변환
     */
    public static BigInteger etherToWei(BigDecimal ether) {
        return Convert.toWei(ether, Convert.Unit.ETHER).toBigInteger();
    }

    /**
     * Gwei를 Wei로 변환
     */
    public static BigInteger gweiToWei(BigDecimal gwei) {
        return gwei.multiply(new BigDecimal("1000000000000000000")).toBigInteger();
    }

    /**
     * ERC20 토큰의 balanceOf 함수 호출
     */
    public static BigInteger getTokenBalance(Web3j web3j, String contractAddress, String walletAddress) throws Exception {
        Function function = new Function(
                "balanceOf",
                Arrays.asList(new Address(walletAddress)),
                Arrays.asList(new TypeReference<Uint256>() {})
        );

        String encodedFunction = FunctionEncoder.encode(function);
        EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(walletAddress, contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST
        ).send();

        List<Type> result = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        return (BigInteger) result.get(0).getValue();
    }

    /**
     * 트랜잭션이 성공했는지 확인
     */
    public static boolean isTransactionSuccess(TransactionReceipt receipt) {
        return receipt != null &&
               receipt.getStatus() != null &&
               receipt.getStatus().equals("0x1");
    }

    /**
     * 주소를 체크섬 주소로 변환
     */
    public static String toChecksumAddress(String address) {
        if (address == null || address.isEmpty()) {
            return address;
        }

        // 0x 접두사 제거
        if (address.startsWith("0x") || address.startsWith("0X")) {
            address = address.substring(2);
        }

        return "0x" + address;
    }

    /**
     * 주소 유효성 검증
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }

        // 0x로 시작하고 42자리여야 함 (0x + 40자리 16진수)
        return address.matches("^0x[0-9a-fA-F]{40}$");
    }

    /**
     * 트랜잭션 해시 유효성 검증
     */
    public static boolean isValidTxHash(String txHash) {
        if (txHash == null || txHash.isEmpty()) {
            return false;
        }

        // 0x로 시작하고 66자리여야 함 (0x + 64자리 16진수)
        return txHash.matches("^0x[0-9a-fA-F]{64}$");
    }
}
