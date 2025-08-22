package com.audigo.audigo_back.controller.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.audigo.audigo_back.controller.app.AuthController;
import com.audigo.audigo_back.dto.request.admin.auth.AdminSignUpRequestDto;
import com.audigo.audigo_back.dto.request.admin.auth.AdminSignInRequestDto;
import com.audigo.audigo_back.dto.response.admin.auth.AdminSignUpResponseDto;
import com.audigo.audigo_back.dto.response.admin.auth.AdminSignInResponseDto;
import com.audigo.audigo_back.service.admin.AdminAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin Auth API", description = "관리자 권한 관련 API")
@RestController
@RequestMapping("/api/adm/auth")
@RequiredArgsConstructor
public class AdminAuthController {
    private static final Log logger = LogFactory.getLog(AdminAuthController.class);

    private final AdminAuthService adminAuthService;

    /**
     * 관리자 최초 등록
     * @param requestBody
     * @return
     */
    @Operation(summary = "최초 관리자 등록", description = "Admin 계정 최초 생성.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "등록성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "DUID", description = "DUPLICATE_ID", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
        @Parameter(name = "id", description = "3 ~ 50자 이내", required = true, example = "test01")
        ,@Parameter(name = "pwd", description = "8 ~ 50자 이내", required = true, example = "12345678")
        ,@Parameter(name = "nm", description = "3 ~ 50자 이내", required = true, example = "최고운영자")
        ,@Parameter(name = "roleCd", description = "3 ~ 30자 이내", required = true, example = "role01")
        ,@Parameter(name = "rememberYn", description = "1자", required = true, example = "Y")
        ,@Parameter(name = "actYn", description = "1자", required = true, example = "Y")
        ,@Parameter(name = "orgCd", description = "조직코드", required = false, example = "O001")
        ,@Parameter(name = "cmpCd", description = "소속사코드", required = false, example = "C001")
        ,@Parameter(name = "deptCd", description = "부서코드", required = false, example = "D001")
        ,@Parameter(name = "mobile", description = "전화번호", required = false, example = "01012345678")
        ,@Parameter(name = "osType", description = "OS 종류", required = false, example = "A")
        ,@Parameter(name = "pushKey", description = "푸시키", required = false, example = "pushKey")
        ,@Parameter(name = "lastIp", description = "마지막 접속 IP", required = false, example = "127.0.0.1")
        ,@Parameter(name = "connInfo", description = "접속정보", required = false, example = "connInfo")
    })
    @PostMapping("/register")
    public ResponseEntity<? super AdminSignUpResponseDto> register(@RequestBody @Valid AdminSignUpRequestDto requestBody) {
        ResponseEntity<? super AdminSignUpResponseDto> response = adminAuthService.register(requestBody);
        return response;
    }




    /**
     * 관리자 로그인
     * @param requestBody
     * @return
     */
    @Operation(summary = "관리자 로그인", description = "Admin 계정 로그인.")
    @ApiResponses({
        @ApiResponse(responseCode = "SU", description = "로그인 성공", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "SIGN_IN_FAIL"),
        @ApiResponse(responseCode = "DE", description = "Database Error", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
        @Parameter(name = "id", description = "AdminSignInRequestDto 참조 3 ~ 50자 이내", required = true, example = "test01"),
        @Parameter(name = "password", description = "8 ~ 50자 이내", required = true, example = "12345678")
    })
    @PostMapping("/sign-in")
    public ResponseEntity<? super AdminSignInResponseDto> signIn(@RequestBody @Valid AdminSignInRequestDto requestBody) {
        logger.info("============ Admin LoginId: " + requestBody.getId());

        ResponseEntity<? super AdminSignInResponseDto> response = adminAuthService.signIn(requestBody);

        return response;
    }

}
