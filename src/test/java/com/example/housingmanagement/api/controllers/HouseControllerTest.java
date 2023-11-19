package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.responses.HouseResponse;
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
        HouseInternalEntity house1 = createEmptyHouse("house1", 3);
        HouseInternalEntity house2 = createFullHouse("house2", 2);

        houseRepository.save(house1);
        houseRepository.save(house2);

        //when
        // Wykonaj żądanie HTTP GET na endpoint /houses
        MvcResult result = mockMvc.perform(get("/houses").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        // Pobierz zawartość odpowiedzi
        String responseContent = result.getResponse().getContentAsString();

        // Mapuj odpowiedź JSON na listę obiektów HouseResponse
        List<HouseResponse> houseResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
        });

        //then
        // Dodaj swoje asercje związane z oczekiwanym wynikiem
        assertEquals(2, houseResponses.size());
        assertEquals("house1", houseResponses.get(0).getHouseNumber());
        assertEquals("house2", houseResponses.get(1).getHouseNumber());
    }

    @Test
    @DisplayName("Should return only partially occupied houses")
    public void getAvailableHousesShouldReturnListOfAvailableHouses() throws Exception {
        //given
        HouseInternalEntity house1 = createPartiallyOccupiedHouse("house1", 3, 1);
        HouseInternalEntity house2 = createFullHouse("house2", 2);

        houseRepository.save(house1);
        houseRepository.save(house2);

        //when
        // Wykonaj żądanie HTTP GET na endpoint /houses
        MvcResult result = mockMvc.perform(get("/houses/available").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        // Pobierz zawartość odpowiedzi
        String responseContent = result.getResponse().getContentAsString();
        List<HouseResponse> houseResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
        });
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

        HouseInternalEntity house1 = createEmptyHouse("house1", 3);
        HouseInternalEntity house2 = createFullHouse("house2", 2);

        houseRepository.save(house1);
        houseRepository.save(house2);

        int id = house1.getId();
        //Hibernate starts indexing from 1! not 0.
        // ALSO: when the database was populated and cleared he continues id assignment!

        //when
        // Wykonaj żądanie HTTP GET na endpoint /houses
        MvcResult result = mockMvc.perform(get("/houses/{id}", id).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        // Pobierz zawartość odpowiedzi
        String responseContent = result.getResponse().getContentAsString();
        HouseResponse houseResponse = objectMapper.readValue(responseContent, new TypeReference<>() {
        });

        //then
        assertEquals("house1", houseResponse.getHouseNumber());
        assertNotEquals("house2", houseResponse.getHouseNumber());
    }

    @Test
    @DisplayName("Should add house to empty database based on provided valid HouseRequest")
    public void addNewHouseShouldAddHouseToEmptyDatabase() throws Exception {
        //given
        HouseRequest houseRequest = createValidHouseRequest("house1", 3);
        String houseRequestJSONString = objectMapper.writeValueAsString(houseRequest);

        //when
        MvcResult result = mockMvc.perform(post("/houses").contentType(MediaType.APPLICATION_JSON).content(houseRequestJSONString)).andExpect(status().isCreated()).andReturn();

        //then
        Assertions.assertEquals(201, result.getResponse().getStatus());
        assertEquals(houseRepository.findAll().get(0).getHouseNumber(), "house1");
    }

    @Test
    @DisplayName("Should add house to a populated database if no house matching HouseRequest exists")
    public void addNewHouseShouldAddHouseToDatabase() throws Exception {
        //given
        //populate database
        houseRepository.save(createEmptyHouse("house0", 2));
        houseRepository.save(createEmptyHouse("house1", 3));

        HouseRequest houseRequest = createValidHouseRequest("house2", 2);
        String houseRequestJSONString = objectMapper.writeValueAsString(houseRequest);

        //when
        MvcResult result = mockMvc.perform(post("/houses").contentType(MediaType.APPLICATION_JSON).content(houseRequestJSONString)).andExpect(status().isCreated()).andReturn();

        //then
        Assertions.assertEquals(201, result.getResponse().getStatus());
        assertEquals(houseRepository.findAll().get(2).getHouseNumber(), "house2");

    }

    @Test
    @DisplayName("Should NOT add house to database which contains house with houseNumber equal to that from HouseRequest")
    public void addNewHouseShouldNotAddHouseToDatabase() throws Exception {
        //given
        //populate database
        houseRepository.save(createEmptyHouse("house0", 3));
        houseRepository.save(createEmptyHouse("house1",3));

        HouseRequest houseRequest = createValidHouseRequest("house1",2);
        String houseRequestJSONString = objectMapper.writeValueAsString(houseRequest);

        //when
        MvcResult result = mockMvc.perform(post("/houses").contentType(MediaType.APPLICATION_JSON).content(houseRequestJSONString)).andExpect(status().isUnprocessableEntity()).andReturn();

        //then
        Assertions.assertEquals(422, result.getResponse().getStatus());
    }

    @Test
    @DisplayName("Should delete a house if the house exists in the database and has no occupants")
    public void deleteSpecificHouseWhenItExistsInDb() throws Exception {
        //given
        houseRepository.save(createEmptyHouse("house0", 3));

        HouseRequest houseRequest = createValidHouseRequest("house0",3);
        String houseRequestJSONString = objectMapper.writeValueAsString(houseRequest);

        //when
        MvcResult result = mockMvc.perform(delete("/houses").contentType(MediaType.APPLICATION_JSON).content(houseRequestJSONString)).andExpect(status().isOk()).andReturn();

        //then
        assertEquals(200, result.getResponse().getStatus());
        assertTrue(houseRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Should NOT delete a house if the house does not exist in the database and has no occupants")
    public void shouldNotDeleteSpecificHouseWhenItDoesNotExistsInDb() throws Exception {
        //given
        houseRepository.save(createEmptyHouse("house0",3));

        HouseRequest houseRequest = createValidHouseRequest("house1",2);
        String houseRequestJSONString = objectMapper.writeValueAsString(houseRequest);

        //when
        MvcResult result = mockMvc.perform(delete("/houses").contentType(MediaType.APPLICATION_JSON).content(houseRequestJSONString)).andExpect(status().isUnprocessableEntity()).andReturn();

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertFalse(houseRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Should NOT delete a house if the house exists in the database but is occupied")
    public void shouldNotDeleteSpecificHouseWhenItDoesExistsInDbButIsOccupied() throws Exception {
        //given
        houseRepository.save(createPartiallyOccupiedHouse("house0",3,2));

        HouseRequest houseRequest = createValidHouseRequest("house0",3);
        String houseRequestJSONString = objectMapper.writeValueAsString(houseRequest);

        //when
        MvcResult result = mockMvc.perform(delete("/houses").contentType(MediaType.APPLICATION_JSON).content(houseRequestJSONString)).andExpect(status().isUnprocessableEntity()).andReturn();

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertFalse(houseRepository.findAll().isEmpty());
    }

    private HouseInternalEntity createEmptyHouse(String houseNumber, int maxCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, 0);
    }

    private HouseInternalEntity createFullHouse(String houseNumber, int maxCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, maxCapacity);
    }
    private HouseInternalEntity createPartiallyOccupiedHouse(String houseNumber, int maxCapacity, int currentCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, currentCapacity);
    }

    private static HouseRequest createValidHouseRequest(String houseNumber, int maxCapacity) {
        return new HouseRequest(houseNumber, 3);
    }

}