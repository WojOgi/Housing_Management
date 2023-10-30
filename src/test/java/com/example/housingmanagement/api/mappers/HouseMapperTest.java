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
    @DisplayName("Display name of test2")
    void test1() {
        //given
        List<HouseInternalEntity> allHouseInternalEntities = null;

        //when
        List<HouseResponse> response = houseMapper.toHouseResponse(allHouseInternalEntities);

        //then
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("Display name of test2")
    void test2() {
        //given
        List<HouseInternalEntity> allHouseInternalEntities = List.of();

        //when
        List<HouseResponse> response = houseMapper.toHouseResponse(allHouseInternalEntities);

        //then
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("Display name of test3")
    void test3() {
        //given
        HouseInternalEntity house1 = new HouseInternalEntity("1", 1, 1);
        HouseInternalEntity house2 = new HouseInternalEntity("2", 2, 2);
        HouseInternalEntity house3 = new HouseInternalEntity("3", 3, 3);

        ArrayList<HouseInternalEntity> arrayList = new ArrayList<>();
        arrayList.add(house1);
        arrayList.add(house2);
        arrayList.add(house2);


        //when
        List<HouseResponse> response = houseMapper.toHouseResponse(arrayList);

        //then
        Assertions.assertFalse(response.isEmpty());
    }

}