package com.example.housingmanagement.api;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface HouseRepositoryJPA extends JpaRepository<HouseInternalEntity, Integer> {

    HouseInternalEntity findByHouseNumber(String houseNumber);

    @Transactional
    void deleteByHouseNumber(String houseNumber);

    boolean existsByHouseNumber(String houseNumber);
}
