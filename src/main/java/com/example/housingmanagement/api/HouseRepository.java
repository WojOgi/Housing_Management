package com.example.housingmanagement.api;

import org.springframework.data.repository.CrudRepository;

public interface HouseRepository extends CrudRepository<HouseInternalEntity, Integer> {

}
