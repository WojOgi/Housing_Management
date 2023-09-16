package com.example.housingmanagement.api.mappers;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.responses.HouseResponse;

import java.util.List;

public interface HouseMapperInterface {

    List<HouseResponse> toHouseResponse(List<HouseInternalEntity> allHouseInternalEntities);

    HouseInternalEntity toHouseInternalEntity(HouseRequest request);
}
