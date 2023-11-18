package com.example.housingmanagement.api.util;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;

import java.time.LocalDateTime;

public class HouseUtil {

    public static HouseInternalEntity getHouse(String houseNumber, int maxCapacity, int currentCapacity) {
        return new HouseInternalEntity(LocalDateTime.now(), houseNumber, maxCapacity, currentCapacity);
    }

    public static HouseInternalEntity getEmptyHouse(String houseNumber, int maxCapacity) {
        return new HouseInternalEntity(LocalDateTime.now(), houseNumber, maxCapacity, 0);
    }

    public static HouseInternalEntity someEmptyHouse(int maxCapacity) {
        return new HouseInternalEntity(LocalDateTime.now(), genereHouseNumber(), maxCapacity, 0);
    }

    private static String genereHouseNumber() {
        return "adhfasdf";
    }

    public static HouseInternalEntity getFullHouse(String houseNumber) {
        return new HouseInternalEntity(LocalDateTime.now(), houseNumber, 2, 2);
    }
}
