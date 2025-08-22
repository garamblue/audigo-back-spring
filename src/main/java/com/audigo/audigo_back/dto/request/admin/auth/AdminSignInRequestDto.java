package com.audigo.audigo_back.dto.request.admin.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminSignInRequestDto {
    //admin sign-in 로그인
    @NotBlank
    @Size(min = 3, max = 50)
    private String id;

    @NotBlank
    @Size(min = 8, max = 100)
    private String pwd;
    
}
