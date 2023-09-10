package com.example.housingmanagement.api.mappers;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.responses.HouseResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HouseMapper implements HouseMapperInterface {

    @Override
    public List<HouseResponse> toHouseResponse(List<HouseInternalEntity> allHouseInternalEntities) {
        List<HouseResponse> houseResponseList = new ArrayList<>();
        for (HouseInternalEntity currentElement : allHouseInternalEntities) {
            HouseResponse houseResponse = new HouseResponse(currentElement.getHouseNumber());
            houseResponseList.add(houseResponse);
        }
        return houseResponseList;
    }

    @Override
    public HouseInternalEntity toHouseInternalEntity(HouseRequest houseToBeAdded) {
        return new HouseInternalEntity(houseToBeAdded.getHouseNumber(),
                houseToBeAdded.getMaxCapacity(), 0);
    }
}
