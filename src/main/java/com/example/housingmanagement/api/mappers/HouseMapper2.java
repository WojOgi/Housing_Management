package com.example.housingmanagement.api.mappers;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.responses.HouseResponse;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class HouseMapper2 implements HouseMapperInterface{
    //this is a mock class to play around with how Spring decides which class to use for implementation



    @Override
    public List<HouseResponse> toHouseResponse(List<HouseInternalEntity> allHouseInternalEntities) {
        return null;
    }

    @Override
    public HouseInternalEntity toHouseInternalEntity(HouseRequest houseToBeAdded) {
        return null;
    }
}
