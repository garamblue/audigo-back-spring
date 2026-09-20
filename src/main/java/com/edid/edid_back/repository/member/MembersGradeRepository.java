package com.audigo.audigo_back.repository.member;

import com.audigo.audigo_back.entity.member.MembersGradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembersGradeRepository extends JpaRepository<MembersGradeEntity, Long> {
}
