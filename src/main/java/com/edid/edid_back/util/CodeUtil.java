package com.audigo.audigo_back.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * 코드 생성 유틸리티
 */
@Component
public class CodeUtil {

    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CHARSET_LEN = CHARSET.length();

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * 랜덤 코드 생성 (영문 대소문자 + 숫자)
     * @param length 코드 길이 (기본 12)
     * @return 랜덤 코드
     */
    public String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(CHARSET_LEN);
            code.append(CHARSET.charAt(index));
        }
        return code.toString();
    }

    /**
     * 랜덤 코드 생성 (기본 12자리)
     */
    public String generateRandomCode() {
        return generateRandomCode(12);
    }

    /**
     * 랜덤 Salt 생성 (Hex)
     * @param lengthBytes 바이트 길이 (기본 32)
     * @return Hex 인코딩된 Salt
     */
    public String generateSalt(int lengthBytes) {
        byte[] salt = new byte[lengthBytes];
        secureRandom.nextBytes(salt);
        return bytesToHex(salt);
    }

    /**
     * 랜덤 Salt 생성 (기본 32바이트)
     */
    public String generateSalt() {
        return generateSalt(32);
    }

    /**
     * SMS 인증 코드 생성 (6자리 숫자)
     * @return 100000 ~ 999999 사이의 숫자
     */
    public String generateAuthCode() {
        int code = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * 바이트 배열을 Hex 문자열로 변환
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
