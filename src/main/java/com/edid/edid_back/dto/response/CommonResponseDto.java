package com.audigo.audigo_back.dto.response;

import com.audigo.audigo_back.common.ResponseCode;
import com.audigo.audigo_back.common.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponseDto<T> {

    private String code;
    private String message;
    private T data;
    
    // 성공 시 사용 (데이터 있음)
    public static <T> CommonResponseDto<T> success(T data) {
        return new CommonResponseDto<>("SU", "SUCCESS", data);
    }

    // 성공 시 사용 (데이터 없음)
    public static <T> CommonResponseDto<T> successWithoutData() {
        return new CommonResponseDto<>("SU", "SUCCESS", null);
    }

    // 실패 시 사용
    public static <T> CommonResponseDto<T> failure(String code, String message) {
        return new CommonResponseDto<>(code, message, null);
    }

    // databaseError 시 사용
    public static <T> CommonResponseDto<T> databaseError() {
        return new CommonResponseDto<>(ResponseCode.DATABASE_ERROR, ResponseMessage.DATABASE_ERROR, null);
    }
}
