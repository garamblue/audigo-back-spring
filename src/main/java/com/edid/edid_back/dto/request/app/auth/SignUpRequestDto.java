package com.audigo.audigo_back.dto.request.app.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequestDto {
    
    @NotBlank
    @Pattern(regexp = "^(0|1)$", message = "0 또는 1이여야 합니다.")
    private String status;

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String nickname;
    @NotBlank
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$", message = "birthDt 형식이 올바르지 않습니다.")
    private String birthDt;
    @NotBlank
    @Pattern(regexp = "^[0-9]{1}$", message = "0~9 여야 합니다.")
    private String snsDiv;
    @NotBlank
    private String osVers;
    @NotBlank
    private String osName;
    @NotBlank
    @Pattern(regexp = "^[0-9]{11,13}$", message = "mobile 번호는 11~13자리 숫자여야 합니다.")
    private String mobileNumb;
    //@NotBlank
    //@Size(min = 8, max = 100)
    //private String password;
    @Pattern(regexp = "^(M|F)$", message = "성별은 M 또는 F여야 합니다.")
    private String gender;
    private String invitationCd;
    private String inviterCd;
    @Pattern(regexp = "^(Y|N)$", message = "Y 또는 N여야 합니다.")
    private String missionYn;
    private String pushTkn;
    @NotBlank
    private String refreshTkn;
    @NotBlank
    private String snsId;
    private String snsVal;
    private String leaveDt;
    private String model;
    private String appVers;
    private String lang;
    private String regionCd;
    @Pattern(regexp = "^(Y|N)$", message = "Y 또는 N여야 합니다.")
    private String pushAlive;

    //@NotBlank
    //private String address;

    //@NotNull
    //@AssertTrue
    //private Boolean agreedPersonal; // 개인정보 수집 및 이용 동의
}