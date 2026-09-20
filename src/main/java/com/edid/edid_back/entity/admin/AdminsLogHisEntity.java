package com.audigo.audigo_back.entity.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 관리자 활동 로그 엔티티
 * 테이블: users.admins_log_his
 *
 * API 호출, 데이터 변경 등 모든 관리자 활동 기록
 */
@Entity
@Table(name = "admins_log_his", schema = "users")
@Getter
@Setter
@NoArgsConstructor
public class AdminsLogHisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alh_idx")
    private BigInteger alhIdx; // 로그 고유 ID

    @Column(name = "a_idx", nullable = false)
    private BigInteger aIdx; // 관리자 ID

    @Column(name = "menu_cd", length = 50)
    private String menuCd; // 메뉴 코드

    @Column(name = "action_type", nullable = false, length = 20)
    private String actionType; // 액션 타입 (VIEW, CREATE, UPDATE, DELETE, LOGIN, LOGOUT)

    @Column(name = "api_url", length = 500)
    private String apiUrl; // API 엔드포인트

    @Column(name = "http_method", length = 10)
    private String httpMethod; // HTTP 메서드 (GET, POST, PUT, DELETE)

    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams; // 요청 파라미터 (JSON)

    @Column(name = "ip_addr", length = 45)
    private String ipAddr; // IP 주소

    @Column(name = "user_agent", length = 500)
    private String userAgent; // User-Agent

    @Column(name = "result_code", length = 10)
    private String resultCode; // 결과 코드 (SUCCESS, FAIL)

    @Column(name = "error_msg", columnDefinition = "TEXT")
    private String errorMsg; // 에러 메시지

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt; // 생성일시

    @PrePersist
    protected void onCreate() {
        cdt = LocalDateTime.now();
    }
}
