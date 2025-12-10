package com.audigo.audigo_back.repository.gamification;

import com.audigo.audigo_back.entity.gamification.HoroscopeDailyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 일일 운세 데이터 Repository
 */
@Repository
public interface HoroscopeDailyRepository extends JpaRepository<HoroscopeDailyEntity, Long> {

    /**
     * 특정 날짜, 별자리/띠, 타입, 언어로 운세 조회
     */
    @Query("SELECT h FROM HoroscopeDailyEntity h WHERE h.date = :date AND h.sign = :sign AND h.tp = :tp AND h.lang = :lang")
    Optional<HoroscopeDailyEntity> findByDateAndSignAndTpAndLang(
            @Param("date") LocalDate date,
            @Param("sign") String sign,
            @Param("tp") String tp,
            @Param("lang") String lang
    );

    /**
     * 특정 날짜의 모든 운세 조회
     */
    @Query("SELECT h FROM HoroscopeDailyEntity h WHERE h.date = :date ORDER BY h.tp, h.sign")
    List<HoroscopeDailyEntity> findByDate(@Param("date") LocalDate date);

    /**
     * 오늘의 운세 조회 (서양 별자리)
     */
    @Query(value = "SELECT * FROM fun.horoscope_daily WHERE date = CURRENT_DATE AND sign = :sign AND tp = 'Z' AND lang = :lang", nativeQuery = true)
    Optional<HoroscopeDailyEntity> findTodayWesternHoroscope(@Param("sign") String sign, @Param("lang") String lang);

    /**
     * 오늘의 운세 조회 (동양 띠)
     */
    @Query(value = "SELECT * FROM fun.horoscope_daily WHERE date = CURRENT_DATE AND sign = :sign AND tp = 'A' AND lang = :lang", nativeQuery = true)
    Optional<HoroscopeDailyEntity> findTodayEasternHoroscope(@Param("sign") String sign, @Param("lang") String lang);
}
