package com.edid.edid_back.entity;

import java.sql.Timestamp;

import com.edid.edid_back.dto.request.admin.auth.AdminSignUpRequestDto;

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
@Table(name = "admins", schema = "users")
public class AdminEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동생성
    @Column(name = "a_idx")
    private Integer aIdx;

    @Column(name = "id")
    private String id;

    @Column(name = "pwd")
    private String pwd;

    @Column(name = "nm")
    private String nm;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "role_cd")
    private String roleCd;

    @Column(name = "act_yn")
    private String actYn;

    @Column(name = "cdt", updatable = false)
    private Timestamp cdt;

    @Column(name = "udt")
    private Timestamp udt;

    @Column(name = "c_aidx")
    private Integer cAidx;

    @PrePersist
    protected void onCreate() {
        cdt = new Timestamp(System.currentTimeMillis());
        udt = new Timestamp(System.currentTimeMillis());
        // lastLoginDt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        udt = new Timestamp(System.currentTimeMillis());
    }

    // API request body 에 실제 보이는 양식
    public AdminEntity(AdminSignUpRequestDto dto) {
        this.id = dto.getId();
        this.pwd = dto.getPwd();
        this.nm = dto.getNm();
        this.mobile = dto.getMobile();
        this.roleCd = dto.getRoleCd();
        this.actYn = dto.getActYn();
        this.cAidx = Integer.parseInt(dto.getCAidx());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "[id: " + id + "]"
                + "[pwd: " + pwd + "]"
                + "[nm: " + nm + "]"
                + "[roleCd: " + roleCd + "]";
    }

}