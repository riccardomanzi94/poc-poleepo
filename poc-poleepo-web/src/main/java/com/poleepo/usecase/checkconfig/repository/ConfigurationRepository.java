package com.poleepo.usecase.checkconfig.repository;

import com.poleepo.usecase.checkconfig.model.entities.ConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<ConfigurationEntity, Long> {

    Optional<ConfigurationEntity> findAllByStoreIdAndSource(Long storeId, Long source);
}
