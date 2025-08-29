package com.audigo.audigo_back.dto.request.app.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequestDto {
    
    @NotBlank
    private String status;

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String nickname;
    @NotBlank
    private String birthDt;
    @NotBlank
    private String snsDiv;
    @NotBlank
    private String osVers;
    @NotBlank
    private String osName;
    @NotBlank
    private String mobileNumb;
    //@NotBlank
    //@Size(min = 8, max = 100)
    //private String password;

    private String snsId;
    private String gender;
    private String invitationCd;
    private String inviterCd;
    private String missionYn;
    private String pushTkn;
    private String snsVal;
    private String loginDt;
    private String logoutDt;
    private String leaveDt;
    private String model;
    private String appVers;
    private String dupliTkn;
    private String lang;
    private String regionCd;
    private String pushAlive;

    //@NotBlank
    //@Pattern(regexp = "^[0-9]{11,13}$", message = "전화번호는 11~13자리 숫자여야 합니다.")
    //private String telNumber;

    //@NotBlank
    //private String address;

    //private String addressDetail;

    //@NotNull
    //@AssertTrue
    //private Boolean agreedPersonal; // 개인정보 수집 및 이용 동의
}