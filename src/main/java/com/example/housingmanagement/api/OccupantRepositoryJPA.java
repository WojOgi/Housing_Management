package com.example.housingmanagement.api;

import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OccupantRepositoryJPA extends JpaRepository<OccupantInternalEntity, Integer> {
    OccupantInternalEntity findByFirstNameAndLastName(String firstName, String lastName);

    void deleteByFirstNameAndLastName(String firstName, String lastName);

    boolean existsByFirstNameAndLastName(String firstName, String lastName);
}
