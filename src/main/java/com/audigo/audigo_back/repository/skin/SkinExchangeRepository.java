package com.audigo.audigo_back.repository.skin;

import com.audigo.audigo_back.entity.skin.SkinExchangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkinExchangeRepository extends JpaRepository<SkinExchangeEntity, Long> {
}
