package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.responses.HouseResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.example.housingmanagement.api.controllers.DataBaseTestUtils.*;
import static com.example.housingmanagement.api.controllers.WebUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HouseControllerTest {

    @Autowired
    private HouseRepositoryJPA houseRepository;

    @BeforeEach
    void setUp() {
        houseRepository.deleteAll();
    }

    @Test
    @DisplayName("getAllHouses should return a list of houses as List<HouseResponse>")
    public void getAllHousesShouldReturnListOfHouses() throws Exception {
        //given
        putIntoHouseDatabase(anEmptyHouse("house1"));
        putIntoHouseDatabase(aFullHouse("house2", 2));

        //when
        List<HouseResponse> houseResponses = getHouseResponseList(performGet("/houses"));

        //then
        assertEquals(2, houseResponses.size());
        assertEquals("house1", houseResponses.get(0).getHouseNumber());
        assertEquals("house2", houseResponses.get(1).getHouseNumber());
    }

    @Test
    @DisplayName("Should return only partially occupied houses")
    public void getAvailableHousesShouldReturnListOfAvailableHouses() throws Exception {
        //given
        putIntoHouseDatabase(aPartiallyOccupiedHouse("house1", 3, 1));
        putIntoHouseDatabase(aFullHouse("house2", 2));

        //when
        List<HouseResponse> houseResponses = getHouseResponseList(performGet("/houses/available"));
        List<String> houseResponsesNames = houseResponses.stream().map(HouseResponse::getHouseNumber).toList();

        //then
        assertFalse(houseResponses.isEmpty());
        assertEquals(1, houseResponses.size());
        assertEquals("house1", houseResponses.get(0).getHouseNumber());
        assertFalse(houseResponsesNames.contains("house2"));
    }

    @Test
    @DisplayName("Should return house with matching id")
    public void getHouseByPathVariableIdShouldReturnHouse() throws Exception {
        //given
        putIntoHouseDatabase(anEmptyHouse("house1"));
        putIntoHouseDatabase(aFullHouse("house2", 2));

        int id = getIdForHouse("house1");

        //when
        HouseResponse houseResponse = getHouseResponse(performGetWithId("/houses/{id}", id));

        //then
        assertEquals("house1", houseResponse.getHouseNumber());
        assertNotEquals("house2", houseResponse.getHouseNumber());
    }

    @Test
    @DisplayName("Should add house to empty database based on provided valid HouseRequest")
    public void addNewHouseShouldAddHouseToEmptyDatabase() throws Exception {
        //given
        HouseRequest houseRequest = createValidHouseRequest("house1", 3);

        //when
        var result = getMvcResultOfPOST(houseRequest, "/houses", status().isCreated());

        //then
        Assertions.assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertEquals(getHouseNumber(0), "house1");
    }

    @Test
    @DisplayName("Should add house to a populated database if no house matching HouseRequest exists")
    public void addNewHouseShouldAddHouseToDatabase() throws Exception {
        //given
        //populate database
        putIntoHouseDatabase(anEmptyHouse("house0"));
        putIntoHouseDatabase(anEmptyHouse("house1"));

        HouseRequest houseRequest = createValidHouseRequest("house2", 2);

        //when
        var result = getMvcResultOfPOST(houseRequest, "/houses", status().isCreated());

        //then
        Assertions.assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertEquals(getHouseNumber(2), "house2");

    }

    @Test
    @DisplayName("Should NOT add house to database which contains house with houseNumber equal to that from HouseRequest")
    public void addNewHouseShouldNotAddHouseToDatabase() throws Exception {
        //given
        //populate database
        putIntoHouseDatabase(anEmptyHouse("house0"));
        putIntoHouseDatabase(anEmptyHouse("house1"));

        HouseRequest houseRequest = createValidHouseRequest("house1", 2);

        //when
        var result = getMvcResultOfPOST(houseRequest, "/houses", status().isUnprocessableEntity());

        //then
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus());
    }

    @Test
    @DisplayName("Should delete a house if the house exists in the database and has no occupants")
    public void deleteSpecificHouseWhenItExistsInDb() throws Exception {
        //given
        putIntoHouseDatabase(anEmptyHouse("house0"));

        HouseRequest houseRequest = createValidHouseRequest("house0", 3);

        //when
        var result = getMvcResultOfDELETE(houseRequest, "/houses", status().isOk());

        //then
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(houseRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Should NOT delete a house if the house does not exist in the database and has no occupants")
    public void shouldNotDeleteSpecificHouseWhenItDoesNotExistsInDb() throws Exception {
        //given
        putIntoHouseDatabase(anEmptyHouse("house0"));

        HouseRequest houseRequest = createValidHouseRequest("house1", 2);

        //when
        var result = getMvcResultOfDELETE(houseRequest, "/houses", status().isUnprocessableEntity());

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus());
        assertFalse(houseRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Should NOT delete a house if the house exists in the database but is occupied")
    public void shouldNotDeleteSpecificHouseWhenItDoesExistsInDbButIsOccupied() throws Exception {
        //given
        putIntoHouseDatabase(DataBaseTestUtils.aPartiallyOccupiedHouse("house0", 3, 2));

        HouseRequest houseRequest = createValidHouseRequest("house0", 3);

        //when
        var result = getMvcResultOfDELETE(houseRequest, "/houses", status().isUnprocessableEntity());

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus());
        assertFalse(houseRepository.findAll().isEmpty());
    }

    private String getHouseNumber(int index) {
        return houseRepository.findAll().get(index).getHouseNumber();
    }

    private int getIdForHouse(String houseNumber) {
        return houseRepository.findByHouseNumber(houseNumber).getId();
    }


    private void putIntoHouseDatabase(HouseInternalEntity houseInternalEntity) {
        houseRepository.save(houseInternalEntity);
    }

}