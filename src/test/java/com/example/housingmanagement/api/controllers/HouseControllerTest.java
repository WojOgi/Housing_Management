package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.responses.HouseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HouseRepositoryJPA houseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        houseRepository.deleteAll();
    }

    @Test
    @DisplayName("getAllHouses should return a list of houses as List<HouseResponse>")
    public void getAllHousesShouldReturnListOfHouses() throws Exception {
        //given
        putIntoHouseDatabase(anEmptyHouse("house1", 3));
        putIntoHouseDatabase(aFullHouse("house2", 2));

        //when
        String responseContent = performGet("/houses");
        List<HouseResponse> houseResponses = getHouseResponseList(responseContent);

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
        String responseContent = performGet("/houses/available");
        List<HouseResponse> houseResponses = getHouseResponseList(responseContent);
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
        putIntoHouseDatabase(anEmptyHouse("house1", 3));
        putIntoHouseDatabase(aFullHouse("house2", 2));

        int id = houseRepository.findByHouseNumber("house1").getId();

        //when
        String responseContent = performGetWithId("/houses/{id}", id);
        HouseResponse houseResponse = getHouseResponse(responseContent);

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
        MvcResult result = getMvcResultOfPOST(houseRequest, "/houses", status().isCreated());

        //then
        Assertions.assertEquals(201, result.getResponse().getStatus());
        assertEquals(houseRepository.findAll().get(0).getHouseNumber(), "house1");
    }

    @Test
    @DisplayName("Should add house to a populated database if no house matching HouseRequest exists")
    public void addNewHouseShouldAddHouseToDatabase() throws Exception {
        //given
        //populate database
        putIntoHouseDatabase(anEmptyHouse("house0", 2));
        putIntoHouseDatabase(anEmptyHouse("house1", 3));

        HouseRequest houseRequest = createValidHouseRequest("house2", 2);

        //when
        MvcResult result = getMvcResultOfPOST(houseRequest, "/houses", status().isCreated());

        //then
        Assertions.assertEquals(201, result.getResponse().getStatus());
        assertEquals(houseRepository.findAll().get(2).getHouseNumber(), "house2");

    }

    @Test
    @DisplayName("Should NOT add house to database which contains house with houseNumber equal to that from HouseRequest")
    public void addNewHouseShouldNotAddHouseToDatabase() throws Exception {
        //given
        //populate database
        putIntoHouseDatabase(anEmptyHouse("house0", 3));
        putIntoHouseDatabase(anEmptyHouse("house1", 3));

        HouseRequest houseRequest = createValidHouseRequest("house1", 2);

        //when
        MvcResult result = getMvcResultOfPOST(houseRequest, "/houses", status().isUnprocessableEntity());

        //then
        Assertions.assertEquals(422, result.getResponse().getStatus());
    }

    @Test
    @DisplayName("Should delete a house if the house exists in the database and has no occupants")
    public void deleteSpecificHouseWhenItExistsInDb() throws Exception {
        //given
        putIntoHouseDatabase(anEmptyHouse("house0", 3));

        HouseRequest houseRequest = createValidHouseRequest("house0", 3);

        //when
        MvcResult result = getMvcResultOfDELETE(houseRequest, "/houses", status().isOk());

        //then
        assertEquals(200, result.getResponse().getStatus());
        assertTrue(houseRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Should NOT delete a house if the house does not exist in the database and has no occupants")
    public void shouldNotDeleteSpecificHouseWhenItDoesNotExistsInDb() throws Exception {
        //given
        putIntoHouseDatabase(anEmptyHouse("house0", 3));

        HouseRequest houseRequest = createValidHouseRequest("house1", 2);

        //when
        MvcResult result = getMvcResultOfDELETE(houseRequest, "/houses", status().isUnprocessableEntity());

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertFalse(houseRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Should NOT delete a house if the house exists in the database but is occupied")
    public void shouldNotDeleteSpecificHouseWhenItDoesExistsInDbButIsOccupied() throws Exception {
        //given
        putIntoHouseDatabase(aPartiallyOccupiedHouse("house0", 3, 2));

        HouseRequest houseRequest = createValidHouseRequest("house0", 3);

        //when
        MvcResult result = getMvcResultOfDELETE(houseRequest, "/houses", status().isUnprocessableEntity());

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertFalse(houseRepository.findAll().isEmpty());
    }

    private String performGet(String url) throws Exception {
        return mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    private String performGetWithId(String url, int id) throws Exception {
        return mockMvc.perform(get(url, id).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    private MvcResult getMvcResultOfPOST(HouseRequest houseRequest, String url, ResultMatcher expectedResult) throws Exception {
        return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(houseRequest))).andExpect(expectedResult).andReturn();
    }

    private MvcResult getMvcResultOfDELETE(HouseRequest houseRequest, String url, ResultMatcher expectedResult) throws Exception {
        return mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(houseRequest))).andExpect(expectedResult).andReturn();
    }

    private List<HouseResponse> getHouseResponseList(String responseContent) throws JsonProcessingException {
        return objectMapper.readValue(responseContent, new TypeReference<>() {
        });
    }

    private HouseResponse getHouseResponse(String responseContent) throws JsonProcessingException {
        return objectMapper.readValue(responseContent, new TypeReference<>() {
        });
    }

    private HouseInternalEntity anEmptyHouse(String houseNumber, int maxCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, 0);
    }

    private HouseInternalEntity aFullHouse(String houseNumber, int maxCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, maxCapacity);
    }

    private HouseInternalEntity aPartiallyOccupiedHouse(String houseNumber, int maxCapacity, int currentCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, currentCapacity);
    }

    private static HouseRequest createValidHouseRequest(String houseNumber, int maxCapacity) {
        return new HouseRequest(houseNumber, maxCapacity);
    }

    private void putIntoHouseDatabase(HouseInternalEntity houseInternalEntity) {
        houseRepository.save(houseInternalEntity);
    }
}