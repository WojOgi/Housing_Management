package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.responses.HouseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.example.housingmanagement.api.util.HouseUtil.someEmptyHouse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class HouseControllerTest {

    @Autowired
    private HouseRepositoryJPA houseRepository;

    @Autowired
    private WebHelperByWojtekOgieglo webHelper;

    @BeforeEach
    void setUp() {
        houseRepository.deleteAll();
    }


    //ctrl + alt + m -> extract method
    //zaznaczenie slowa --> alt + j -> zaznacza kolejne występowanie tego słowa
    // alt + shift + j -> cofa zaznaczenie
    // zaznacz logiczną całość (np skończoną implementacja co cos robi) -> ctrl + alt + m ->
    // ctrl + alt + n => inline variable (przy house1 itp)

    @Test
    @DisplayName("getAllHouses should return a list of all houses")
    public void getAllHousesShouldReturnListOfHouses() throws Exception {
        //given
        thereIs(someEmptyHouse( 3));
        thereIs(someEmptyHouse( 2));

        //when
        List<HouseResponse> response = webHelper.performGet("/houses");

        //then
        assertEquals(2, response.size());
        assertEquals("House1", response.get(0).getHouseNumber());
        assertEquals("House2", response.get(1).getHouseNumber());
    }


    private void thereIs(HouseInternalEntity house) {
        houseRepository.save(house);
    }
}