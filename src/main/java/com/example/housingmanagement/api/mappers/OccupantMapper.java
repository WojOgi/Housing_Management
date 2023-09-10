package com.example.housingmanagement.api.mappers;

import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.responses.OccupantResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OccupantMapper implements OccupantMapperInterface {
    @Override
    public List<OccupantResponse> toOccupantResponse(List<OccupantInternalEntity> occupantInternalEntityList) {
        List<OccupantResponse> occupantResponseList = new ArrayList<>();
        for (OccupantInternalEntity currentElement : occupantInternalEntityList) {
            OccupantResponse occupantResponse = new OccupantResponse(currentElement.getFirstName(), currentElement.getLastName());
            occupantResponseList.add(occupantResponse);
        }
        return occupantResponseList;
    }

    @Override
    public OccupantInternalEntity toOccupantInternalEntity(OccupantRequest occupantToBeAddedWithoutHouse) {
        return new OccupantInternalEntity(occupantToBeAddedWithoutHouse.getFirstName(), occupantToBeAddedWithoutHouse.getLastName()
                , occupantToBeAddedWithoutHouse.getGender());
    }
}
