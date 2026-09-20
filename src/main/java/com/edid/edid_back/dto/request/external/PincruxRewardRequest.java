package com.audigo.audigo_back.dto.request.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pincrux 보상 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PincruxRewardRequest {
    private String appkey;
    private Integer pubkey;
    private String usrkey;      // 유저 고유 키 (ext_key)
    private String app_title;
    private Integer coin;
    private String transid;
    private String resign_flag;
    private String commission;
}
