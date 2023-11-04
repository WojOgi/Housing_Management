package com.example.housingmanagement.api.mappers;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.responses.HouseResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class HouseMapperTest {

    private final HouseMapper houseMapper = new HouseMapper();
    private static final List<HouseInternalEntity> listWithoutNulls = new ArrayList<>();
    private static final List<HouseInternalEntity> listWithOneNull = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        listWithoutNulls.add(createHouseInternalEntity(LocalDateTime.now(), "House1", 3, 0));
        listWithoutNulls.add(createHouseInternalEntity(LocalDateTime.now(), "House2", 2, 0));
        listWithoutNulls.add(createHouseInternalEntity(LocalDateTime.now(), "House3", 1, 0));

        listWithOneNull.add(createHouseInternalEntity(LocalDateTime.now(), "House1", 3, 0));
        listWithOneNull.add(createHouseInternalEntity(LocalDateTime.now(), "House2", 2, 0));
        listWithOneNull.add(null);
    }

    @Test
    @DisplayName("Should return empty response when null parameter is provided")
    void test1() {
        //given
        List<HouseInternalEntity> houseInternalEntityList = null;
        //when
        List<HouseResponse> houseResponseList = houseMapper.toHouseResponseList(houseInternalEntityList);
        //then
        Assertions.assertTrue(houseResponseList.isEmpty());
    }

    @Test
    @DisplayName("Should return empty response when empty list is provided")
    void test2() {
        //given
        List<HouseInternalEntity> houseInternalEntityList = List.of();
        //when
        List<HouseResponse> houseResponseList = houseMapper.toHouseResponseList(houseInternalEntityList);
        //then
        Assertions.assertTrue(houseResponseList.isEmpty());
    }

    @Test
    @DisplayName("Should successfully map provided list of houses without any null values")
    void test3() {
        //given
        List<HouseInternalEntity> inputList = listWithoutNulls;
        //when
        List<HouseResponse> responseList = houseMapper.toHouseResponseList(inputList);
        //then
        Assertions.assertFalse(responseList.isEmpty());
        Assertions.assertEquals(inputList.size(), responseList.size());
        for (int i = 0; i < inputList.size(); i++) {
            Assertions.assertEquals(responseList.get(i).getHouseNumber(), inputList.get(i).getHouseNumber());
        }
    }

    @Test
    @DisplayName("Should successfully map provided list of houses containing one null value")
    void test4() {
        //given
        List<HouseInternalEntity> inputList = listWithOneNull;
        //when
        List<HouseResponse> responseList = houseMapper.toHouseResponseList(inputList);
        //then
        Assertions.assertFalse(responseList.isEmpty());
        Assertions.assertEquals(inputList.size() - 1, responseList.size());
        for (int i = 0; i < inputList.size(); i++) {
            if (inputList.get(i) != null) {
                Assertions.assertEquals(responseList.get(i).getHouseNumber(), inputList.get(i).getHouseNumber());
            }
        }
    }

    private static HouseInternalEntity createHouseInternalEntity(LocalDateTime localDateTime, String houseNumber,
                                                                 int maxCapacity, int currentCapacity) {
        return new HouseInternalEntity(localDateTime, houseNumber, maxCapacity, currentCapacity);
    }

}