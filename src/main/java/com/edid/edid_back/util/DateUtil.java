package com.audigo.audigo_back.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

/**
 * 날짜 유틸리티
 */
@Component
public class DateUtil {

    /**
     * 성인 여부 확인 (만 19세 이상)
     * @param birthDate 생년월일
     * @return true=성인, false=미성년자
     */
    public boolean isAdult(LocalDate birthDate) {
        if (birthDate == null) {
            return false;
        }

        LocalDate now = LocalDate.now();
        Period age = Period.between(birthDate, now);

        return age.getYears() >= 19;
    }

    /**
     * 탈퇴 후 1개월 이내 여부 확인
     * @param leaveDate 탈퇴일
     * @return true=1개월 이내, false=1개월 이상
     */
    public boolean isWithinOneMonth(LocalDate leaveDate) {
        if (leaveDate == null) {
            return false;
        }

        LocalDate now = LocalDate.now();
        Period period = Period.between(leaveDate, now);

        // 1개월 미만이면 true
        return period.getMonths() < 1 && period.getYears() == 0;
    }
}
