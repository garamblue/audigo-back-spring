package com.audigo.audigo_back.controller.app;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audigo.audigo_back.dto.request.app.auth.SignInRequestDto;
import com.audigo.audigo_back.dto.request.app.auth.SignUpRequestDto;
import com.audigo.audigo_back.dto.response.app.auth.SignInResponseDto;
import com.audigo.audigo_back.dto.response.app.auth.SignUpResponseDto;
import com.audigo.audigo_back.service.app.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = " Member Auth API", description = "사용자 권한 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     * @param requestBody
     * @return
     */
    @Operation(summary = "최초 회원가입", description = "최초 회원가입 시 사용자 정보를 등록.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "가입성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "DE", description = "DUPLICATE_EMAIL", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "DN", description = "DUPLICATE_NICKNAME", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "DT", description = "DUPLICATE_TEL_NUMBER", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/sign-up")
    public ResponseEntity<? super SignUpResponseDto> signUp(@RequestBody @Valid SignUpRequestDto requestBody) {
        ResponseEntity<? super SignUpResponseDto> response = authService.signUp(requestBody);
        return response;
    }

    /**
     * 로그인
     * @param requestBody
     * @return
     */
    @Operation(summary = "로그인", description = "로그인 시 사용자 mail 정보를 전달.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "로그인 성공"),
        @ApiResponse(responseCode = "401", description = "SIGN_IN_FAIL")
    })
    @Parameters({
        @Parameter(name = "email", description = "이메일", example = "email2@email.com"),
        @Parameter(name = "password", description = "6자~12자 이내", example = "password")
    })
    @PostMapping("/sign-in")
    public ResponseEntity<? super SignInResponseDto> signIn(@RequestBody @Valid SignInRequestDto requestBody) {
        log.info("=============== getEmail: " + requestBody.getEmail());

        ResponseEntity<? super SignInResponseDto> response = authService.signIn(requestBody);

        return response;
    }

}
