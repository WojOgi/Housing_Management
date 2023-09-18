package com.example.housingmanagement.api.mappers;

import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.responses.OccupantResponse;

import java.util.List;

public interface OccupantMapperInterface {

    List<OccupantResponse> toOccupantResponse(List<OccupantInternalEntity> occupantInternalEntityList);

    OccupantInternalEntity toOccupantInternalEntity(OccupantRequest request);
}
