package com.example.housingmanagement.api.utils;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.OccupantRepositoryJPA;
import com.example.housingmanagement.api.dbentities.Gender;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.requests.OccupantRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class DatabasePupulator {
    //TODO implement also occupant to house assignment methods
    //TODO maybe I do not need a class DatabasePopulator - because the AssignmentControllerTest class
    //will have its own methods 

    @Autowired
    private OccupantRepositoryJPA occupantRepository;

    @Autowired
    private HouseRepositoryJPA houseRepository;

    private static final LocalDateTime now = LocalDateTime.now();

    public HouseInternalEntity anEmptyHouse(String houseNumber, int maxCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, 0);
    }

    public HouseInternalEntity aFullHouse(String houseNumber, int maxCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, maxCapacity);
    }

    public HouseInternalEntity aPartiallyOccupiedHouse(String houseNumber, int maxCapacity, int currentCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, currentCapacity);
    }

    public static HouseRequest createValidHouseRequest(String houseNumber, int maxCapacity) {
        return new HouseRequest(houseNumber, 3);
    }

    public void putIntoHouseDatabase(HouseInternalEntity houseInternalEntity) {
        houseRepository.save(houseInternalEntity);
    }

    public OccupantInternalEntity maleOccupant(String firstName, String lastName) {
        return new OccupantInternalEntity(now, firstName, lastName, Gender.MALE);
    }

    public OccupantInternalEntity femaleOccupant (String firstName, String lastName) {
        return new OccupantInternalEntity(now, firstName, lastName, Gender.FEMALE);
    }
    public static OccupantRequest createValidOccupantRequest(String firstName, String lastName, Gender gender) {
        return new OccupantRequest(firstName, lastName, gender);
    }

    public void putIntoOccupantRepository(OccupantInternalEntity occupantInternalEntity){
        occupantRepository.save(occupantInternalEntity);
    }
}
