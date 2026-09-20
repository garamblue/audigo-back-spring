package com.audigo.audigo_back.util;

import java.security.MessageDigest;

public class HashUtil {
    
    /**
     * 평문 문자열을 SHA-256으로 암호화
     * @param plainText 암호화할 평문
     * @return SHA-256으로 암호화된 문자열 (64자리 16진수)
     * SHA-256 암호화 (64자리 16진수)
     * String encrypted = HashUtil.encryptSHA256("mypassword");
     */
    public static String encryptSHA256(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainText.getBytes("UTF-8"));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 암호화 실패", e);
        }
    }
    
    /**
     * 평문 문자열을 SHA-1으로 암호화 (160비트)
     * @param plainText 암호화할 평문
     * @return SHA-1으로 암호화된 문자열 (40자리 16진수)
     * SHA-1 암호화 (40자리 16진수) 
     * String encrypted = HashUtil.encryptSHA1("mypassword");
     */
    public static String encryptSHA1(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(plainText.getBytes("UTF-8"));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("SHA-1 암호화 실패", e);
        }
    }
}