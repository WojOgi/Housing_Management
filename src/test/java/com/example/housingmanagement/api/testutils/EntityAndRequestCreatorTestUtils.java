package com.example.housingmanagement.api.testutils;

import com.example.housingmanagement.api.dbentities.Gender;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.requests.OccupantRequest;

import java.time.LocalDateTime;

public class EntityAndRequestCreatorTestUtils {

    private static final LocalDateTime now = LocalDateTime.now();

    public static HouseRequest createValidHouseRequest(String houseNumber, int maxCapacity) {
        return new HouseRequest(houseNumber, maxCapacity);
    }

    public static HouseRequest createValidHouseRequest(String houseNumber) {
        return new HouseRequest(houseNumber);
    }

    public static OccupantRequest createValidOccupantRequest(String firstName, String lastName, Gender gender) {
        return new OccupantRequest(firstName, lastName, gender);
    }

    public static HouseInternalEntity anEmptyHouse(String houseNumber) {
        return new HouseInternalEntity(now, houseNumber, 3, 0);
    }

    public static HouseInternalEntity aFullHouse(String houseNumber, int maxCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, maxCapacity);
    }

    public static HouseInternalEntity aPartiallyOccupiedHouse(String houseNumber, int maxCapacity, int currentCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, currentCapacity);
    }

    public static OccupantInternalEntity maleOccupant(String firstName, String lastName) {
        return new OccupantInternalEntity(now, firstName, lastName, Gender.MALE);
    }

    public static OccupantInternalEntity femaleOccupant(String firstName, String lastName) {
        return new OccupantInternalEntity(now, firstName, lastName, Gender.FEMALE);
    }


}
