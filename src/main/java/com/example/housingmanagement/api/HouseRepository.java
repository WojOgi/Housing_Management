package com.example.housingmanagement.api;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import org.springframework.data.repository.CrudRepository;

public interface HouseRepository extends CrudRepository<HouseInternalEntity, Integer> {

}
