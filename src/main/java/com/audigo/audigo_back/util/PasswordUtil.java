package com.audigo.audigo_back.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * 비밀번호 생성 및 검증 유틸리티
 */
@Component
public class PasswordUtil {

    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int DEFAULT_LENGTH = 10;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * 랜덤 비밀번호 생성
     * @param length 비밀번호 길이 (기본 10)
     * @return 랜덤 비밀번호
     */
    public String generateRandomPassword(int length) {
        if (length < 10) {
            throw new IllegalArgumentException("Password length must be at least 10");
        }

        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = Math.abs(randomBytes[i]) % PASSWORD_CHARS.length();
            password.append(PASSWORD_CHARS.charAt(index));
        }

        return password.toString();
    }

    /**
     * 랜덤 비밀번호 생성 (기본 길이 10)
     */
    public String generateRandomPassword() {
        return generateRandomPassword(DEFAULT_LENGTH);
    }

    /**
     * 비밀번호 검증 결과 DTO
     */
    @Data
    @AllArgsConstructor
    public static class ValidationResult {
        private boolean valid;
        private String message;

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult fail(String message) {
            return new ValidationResult(false, message);
        }
    }

    /**
     * 비밀번호 유효성 검증
     * - 10자리 이상
     * - 영문, 숫자, 특수문자 모두 포함
     * - 공백 불가
     * - 허용된 특수문자만 사용 (!@#$%^&*)
     *
     * @param password 검증할 비밀번호
     * @return ValidationResult
     */
    public ValidationResult validatePassword(String password) {
        // 1. Null 체크
        if (password == null || password.isEmpty()) {
            return ValidationResult.fail("비밀번호를 입력해주세요.");
        }

        // 2. 공백 체크
        if (password.contains(" ")) {
            return ValidationResult.fail("비밀번호에는 공백을 포함할 수 없습니다.");
        }

        // 3. 길이 체크 (10자리 이상)
        if (password.length() < 10) {
            return ValidationResult.fail("비밀번호는 10자리 이상이어야 합니다.");
        }

        // 4. 허용된 문자만 사용했는지 체크
        Pattern allowedPattern = Pattern.compile("^[A-Za-z0-9!@#$%^&*]+$");
        if (!allowedPattern.matcher(password).matches()) {
            return ValidationResult.fail("비밀번호에는 허용되지 않은 특수문자가 포함되어 있습니다.");
        }

        // 5. 영문 포함 여부
        boolean hasLetter = Pattern.compile("[A-Za-z]").matcher(password).find();
        if (!hasLetter) {
            return ValidationResult.fail("비밀번호는 영문을 포함해야 합니다.");
        }

        // 6. 숫자 포함 여부
        boolean hasNumber = Pattern.compile("[0-9]").matcher(password).find();
        if (!hasNumber) {
            return ValidationResult.fail("비밀번호는 숫자를 포함해야 합니다.");
        }

        // 7. 특수문자 포함 여부
        boolean hasSpecial = Pattern.compile("[!@#$%^&*]").matcher(password).find();
        if (!hasSpecial) {
            return ValidationResult.fail("비밀번호는 특수문자를 포함해야 합니다.");
        }

        // 모든 조건 통과
        return ValidationResult.success();
    }

    /**
     * 비밀번호가 유효한지 boolean으로 반환
     */
    public boolean isValidPassword(String password) {
        return validatePassword(password).isValid();
    }
}
