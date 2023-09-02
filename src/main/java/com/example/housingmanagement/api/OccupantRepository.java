package com.example.housingmanagement.api;

import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import org.springframework.data.repository.CrudRepository;

public interface OccupantRepository extends CrudRepository<OccupantInternalEntity, Integer> {
}
