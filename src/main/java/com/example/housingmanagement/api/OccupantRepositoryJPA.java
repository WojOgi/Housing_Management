package com.example.housingmanagement.api;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OccupantRepositoryJPA extends JpaRepository<HouseInternalEntity, Integer> {
}
