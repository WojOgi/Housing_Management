package com.example.housingmanagement.api.mappers;

import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.responses.OccupantResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class OccupantMapper implements OccupantMapperInterface {
    @Override
    public List<OccupantResponse> toOccupantResponseList(List<OccupantInternalEntity> occupantInternalEntityList) {
        return Optional.ofNullable(occupantInternalEntityList).orElse(List.of()).stream()
                .filter(Objects::nonNull)
                .map(entity -> new OccupantResponse(entity.getFirstName(), entity.getLastName()))
                .toList();
    }

    @Override
    public OccupantInternalEntity toOccupantInternalEntity(OccupantRequest request) {
        return new OccupantInternalEntity(request.getFirstName(), request.getLastName(), request.getGender());
    }

    @Override
    public OccupantResponse toOccupantResponseList(OccupantInternalEntity occupantInternalEntity) {
        return new OccupantResponse(occupantInternalEntity.getFirstName(), occupantInternalEntity.getLastName());
    }
}
