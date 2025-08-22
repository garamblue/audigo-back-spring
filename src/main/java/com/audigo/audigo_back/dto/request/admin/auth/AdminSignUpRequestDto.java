package com.audigo.audigo_back.dto.request.admin.auth;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminSignUpRequestDto {
    //admin sign-up 가입
    private String orgCd;  //조직코드
    private String cmpCd;  //소속회사코드
    private String deptCd; //부서코드

    @NotBlank
    @Size(min = 3, max = 50)
    private String id;

    @NotBlank
    @Size(min = 8, max = 100)
    private String pwd;

    @NotBlank
    @Size(min = 3, max = 50)
    private String nm;    //이름

    @Pattern(regexp = "^[0-9]{11,13}$", message = "전화번호는 11~13자리 숫자여야 합니다.")
    private String mobile;

    private String osType;  //접속기기 OS
    private String pushKey; //push token

    @NotBlank
    @Size(min = 3, max = 30)
    private String roleCd;  //관리자 역할코드
    private String lastIp;  //마지막 접속 IP
    private String connInfo; //접속브라우저 정보

    @NotBlank
    @Size(min = 1, max = 1)
    private String rememberYn; //자동로그인 여부

    @NotBlank
    @Size(min = 1, max = 1)
    private String actYn;   //계정 활성화 여부

}