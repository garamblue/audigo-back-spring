package com.audigo.audigo_back.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.audigo.audigo_back.dto.response.ResponseDto;

@RestControllerAdvice
public class BadRequestExceptionHandler {
    private static final Log logger = LogFactory.getLog(BadRequestExceptionHandler.class);

    @ExceptionHandler({ MethodArgumentNotValidException.class, HttpMessageNotReadableException.class })
    public ResponseEntity<ResponseDto> validationExceptionHandler(Exception exception) {
        logger.info("validationExceptionHandler: " + exception);
        return ResponseDto.validationFailed();
    }

}
