package com.audigo.audigo_back.entity;


import java.sql.Timestamp;

import com.audigo.audigo_back.dto.request.admin.auth.AdminSignUpRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "admin")
@Table(name = "admin", schema = "users")
public class AdminEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//자동생성
    @Column(name = "a_idx")
    private Integer aIdx;

    @Column(name = "org_cd")
    private String orgCd;

    @Column(name = "cmp_cd")
    private String cmpCd;

    @Column(name = "dept_cd")
    private String deptCd;

    @Column(name = "id")
    private String id;

    @Column(name = "pwd")
    private String pwd;

    @Column(name = "nm")
    private String nm;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "os_type")
    private String osType;

    @Column(name = "push_key")
    private String pushKey;

    @Column(name = "role_cd")
    private String roleCd;

    @Column(name = "last_ip")
    private String lastIp;

    @Column(name = "last_login_dt")
    private Timestamp lastLoginDt;

    @Column(name = "conn_info")
    private String connInfo;

    @Column(name = "remember_yn")
    private String rememberYn;

    @Column(name = "act_yn")
    private String actYn;

    @Column(name = "cdt", updatable = false)
    private Timestamp cdt;

    @Column(name = "udt")
    private Timestamp udt;

    @PrePersist
    protected void onCreate() {
        cdt = new Timestamp(System.currentTimeMillis());
        udt = new Timestamp(System.currentTimeMillis());
        lastLoginDt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        udt = new Timestamp(System.currentTimeMillis());
    }

    // API request body 에 실제 보이는 양식
    public AdminEntity(AdminSignUpRequestDto dto) {
        this.orgCd = dto.getOrgCd();
        this.cmpCd = dto.getCmpCd();
        this.deptCd = dto.getDeptCd();
        this.id = dto.getId();
        this.pwd = dto.getPwd();
        this.nm = dto.getNm();
        this.mobile = dto.getMobile();
        this.osType = dto.getOsType();
        this.pushKey = dto.getPushKey();
        this.roleCd = dto.getRoleCd();
        this.lastIp = dto.getLastIp();
        this.connInfo = dto.getConnInfo();
        this.rememberYn = dto.getRememberYn();
        this.actYn = dto.getActYn();
    }


    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "[id: " + id + "]"
                + "[pwd: " + pwd + "]"
                + "[nm: " + nm + "]"
                + "[roleCd: " + roleCd + "]"
                + "[lastIp: " + lastIp + "]"
                + "[lastLoginDt: " + lastLoginDt + "]"
                + "[rememberYn: " + rememberYn + "]";
    }

}