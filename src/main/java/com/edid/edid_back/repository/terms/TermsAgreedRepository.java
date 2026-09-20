package com.audigo.audigo_back.repository.terms;

import com.audigo.audigo_back.entity.terms.TermsAgreedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsAgreedRepository extends JpaRepository<TermsAgreedEntity, Long> {
}
