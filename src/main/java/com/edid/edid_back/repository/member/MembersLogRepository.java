package com.audigo.audigo_back.repository.member;

import com.audigo.audigo_back.entity.member.MembersLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembersLogRepository extends JpaRepository<MembersLogEntity, Long> {
}
