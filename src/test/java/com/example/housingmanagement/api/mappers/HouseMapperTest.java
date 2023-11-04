package com.example.housingmanagement.api.mappers;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.responses.HouseResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class HouseMapperTest {

    private final HouseMapper houseMapper = new HouseMapper();

    @Test
    @DisplayName("Should return empty response when null parameter was provided")
    void test() {
        //given
        List<HouseInternalEntity> allHouseInternalEntities = null;

        //when
        List<HouseResponse> response = houseMapper.toHouseResponse(allHouseInternalEntities);

        //then
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("Should return empty response when empty list was provided")
    void test2() {
        //given
        List<HouseInternalEntity> allHouseInternalEntities = List.of();

        //when
        List<HouseResponse> response = houseMapper.toHouseResponse(allHouseInternalEntities);

        //then
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("Should successfully map provided list of houses")
    void test3() {
        //given
        List<HouseInternalEntity> input = List.of(
                getHouseInternalEntity("1", 1, 1),
                getHouseInternalEntity("2", 2, 2),
                getHouseInternalEntity("3", 3, 3));

        //when
        List<HouseResponse> response = houseMapper.toHouseResponse(input);

        //then
        Assertions.assertFalse(response.isEmpty());
        Assertions.assertEquals(3, response.size());
        Assertions.assertEquals(response.get(0).getHouseNumber(), input.get(0).getHouseNumber());
    }

    @Test
    @DisplayName("xxxx")
    void test4() {
        //given
        List<HouseInternalEntity> input = new ArrayList<>();
        input.add(getHouseInternalEntity("1", 1, 1));
        input.add(null);

        //when
        List<HouseResponse> response = houseMapper.toHouseResponse(input);

        //then
        Assertions.assertFalse(response.isEmpty());
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(response.get(0).getHouseNumber(), input.get(0).getHouseNumber());
    }


    private static HouseInternalEntity getHouseInternalEntity(String houseNumber, int maxCapacity, int currentCapacity) {
        return new HouseInternalEntity(houseNumber, maxCapacity, currentCapacity);
    }

}