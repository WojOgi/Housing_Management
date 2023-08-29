package com.example.housingmanagement.api;

import org.springframework.data.repository.CrudRepository;

public interface OccupantRepository extends CrudRepository<OccupantInternalEntity, Integer> {
}
