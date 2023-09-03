package com.example.housingmanagement.api;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepositoryJPA extends JpaRepository<HouseInternalEntity, Integer> {

    HouseInternalEntity findByHouseNumber(String houseNumber);

    void deleteByHouseNumber(String houseNumber);
    boolean existsByHouseNumber(String houseNumber);
}
