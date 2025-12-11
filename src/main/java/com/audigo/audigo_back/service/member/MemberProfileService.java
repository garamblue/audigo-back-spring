package com.audigo.audigo_back.service.member;

import com.audigo.audigo_back.entity.member.MembersEntity;
import com.audigo.audigo_back.repository.member.MembersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

/**
 * 회원 프로필 관리 서비스
 * - 닉네임 변경
 * - 지역 상태 변경
 * - 휴대폰 번호 변경
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberProfileService {

    private final MembersRepository membersRepository;

    /**
     * 닉네임 중복 확인
     */
    @Transactional(readOnly = true)
    public boolean checkNicknameDuplicate(String nickname) {
        return membersRepository.existsByNickname(nickname);
    }

    /**
     * 닉네임 변경
     */
    @Transactional
    public void updateNickname(BigInteger mIdx, String newNickname) {
        // 1. 회원 확인
        MembersEntity member = membersRepository.findById(mIdx)
                .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));

        // 탈퇴 회원 확인
        if ("3".equals(member.getStts()) || member.getLvDt() != null) {
            throw new IllegalStateException("탈퇴한 회원입니다.");
        }

        // 2. 닉네임 중복 확인
        if (membersRepository.existsByNickname(newNickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 3. 닉네임 업데이트
        member.setNickname(newNickname);
        membersRepository.save(member);

        log.info("닉네임 변경 성공: mIdx={}, newNickname={}", mIdx, newNickname);
    }

    /**
     * 지역 상태 변경
     */
    @Transactional
    public void updateState(BigInteger mIdx, String newState) {
        // 1. 회원 확인
        MembersEntity member = membersRepository.findById(mIdx)
                .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));

        // 탈퇴 회원 확인
        if ("3".equals(member.getStts()) || member.getLvDt() != null) {
            throw new IllegalStateException("탈퇴한 회원입니다.");
        }

        // 2. 상태 업데이트
        member.setState(newState);
        membersRepository.save(member);

        log.info("지역 상태 변경 성공: mIdx={}, newState={}", mIdx, newState);
    }

    /**
     * 휴대폰 번호 변경
     */
    @Transactional
    public void updateMobileNum(BigInteger mIdx, String newMobileNum) {
        // 1. 회원 확인
        MembersEntity member = membersRepository.findById(mIdx)
                .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));

        // 탈퇴 회원 확인
        if ("3".equals(member.getStts()) || member.getLvDt() != null) {
            throw new IllegalStateException("탈퇴한 회원입니다.");
        }

        // 2. 휴대폰 번호 업데이트
        member.setMobileNum(newMobileNum);
        membersRepository.save(member);

        log.info("휴대폰 번호 변경 성공: mIdx={}", mIdx);
    }
}
