package com.audigo.audigo_back.repository.admin;

import com.audigo.audigo_back.entity.admin.AdminsDetectedDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminsDetectedDeviceRepository extends JpaRepository<AdminsDetectedDeviceEntity, BigInteger> {

    /**
     * 관리자 ID로 기기 목록 조회
     */
    List<AdminsDetectedDeviceEntity> findByAIdx(BigInteger aIdx);

    /**
     * 관리자의 승인된 기기 목록 조회
     */
    @Query("SELECT d FROM AdminsDetectedDeviceEntity d " +
           "WHERE d.aIdx = :aIdx AND d.approvedYn = 'Y' " +
           "ORDER BY d.approvedDt DESC")
    List<AdminsDetectedDeviceEntity> findApprovedDevicesByAIdx(@Param("aIdx") BigInteger aIdx);

    /**
     * 관리자의 승인 대기 기기 목록 조회
     */
    @Query("SELECT d FROM AdminsDetectedDeviceEntity d " +
           "WHERE d.aIdx = :aIdx AND d.approvedYn = 'N' " +
           "ORDER BY d.cdt DESC")
    List<AdminsDetectedDeviceEntity> findPendingDevicesByAIdx(@Param("aIdx") BigInteger aIdx);

    /**
     * 모든 승인 대기 기기 목록 조회
     */
    List<AdminsDetectedDeviceEntity> findByApprovedYnOrderByCdtDesc(String approvedYn);

    /**
     * 관리자, 기기 ID, IP로 기기 조회
     */
    @Query("SELECT d FROM AdminsDetectedDeviceEntity d " +
           "WHERE d.aIdx = :aIdx " +
           "AND d.deviceId = :deviceId " +
           "AND d.ipAddr = :ipAddr")
    Optional<AdminsDetectedDeviceEntity> findByAIdxAndDeviceIdAndIpAddr(
            @Param("aIdx") BigInteger aIdx,
            @Param("deviceId") String deviceId,
            @Param("ipAddr") String ipAddr);

    /**
     * 관리자의 특정 기기/IP가 승인되었는지 확인
     */
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
           "FROM AdminsDetectedDeviceEntity d " +
           "WHERE d.aIdx = :aIdx " +
           "AND d.deviceId = :deviceId " +
           "AND d.ipAddr = :ipAddr " +
           "AND d.approvedYn = 'Y'")
    boolean isDeviceApproved(@Param("aIdx") BigInteger aIdx,
                              @Param("deviceId") String deviceId,
                              @Param("ipAddr") String ipAddr);
}
