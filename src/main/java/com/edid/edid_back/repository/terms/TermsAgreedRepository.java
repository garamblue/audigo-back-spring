package com.edid.edid_back.repository.terms;

import com.edid.edid_back.entity.terms.TermsAgreedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsAgreedRepository extends JpaRepository<TermsAgreedEntity, Long> {
}
