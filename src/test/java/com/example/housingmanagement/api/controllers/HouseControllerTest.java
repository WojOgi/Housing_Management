package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.responses.HouseResponse;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        HouseInternalEntity house1 = new HouseInternalEntity(now, "House1", 3, 0);
        HouseInternalEntity house2 = new HouseInternalEntity(now, "House2", 2, 0);

        houseRepository.save(house1);
        houseRepository.save(house2);

        //when
        // Wykonaj żądanie HTTP GET na endpoint /houses
        MvcResult result = mockMvc.perform(get("/houses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Pobierz zawartość odpowiedzi
        String responseContent = result.getResponse().getContentAsString();

        // Mapuj odpowiedź JSON na listę obiektów HouseResponse
        List<HouseResponse> houseResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
        });

        //then
        // Dodaj swoje asercje związane z oczekiwanym wynikiem
        assertEquals(2, houseResponses.size());
        assertEquals("House1", houseResponses.get(0).getHouseNumber());
        assertEquals("House2", houseResponses.get(1).getHouseNumber());
    }

    @Test
    @DisplayName("Should return only houses that have currentCapacity < maxCapacity")
    public void getAvailableHousesShouldReturnListOfAvailableHouses() throws Exception {
        //given
        HouseInternalEntity house1 = new HouseInternalEntity(now, "House1", 3, 0);
        HouseInternalEntity house2 = new HouseInternalEntity(now, "House2", 2, 2);

        houseRepository.save(house1);
        houseRepository.save(house2);

        //when
        // Wykonaj żądanie HTTP GET na endpoint /houses
        MvcResult result = mockMvc.perform(get("/houses/available").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        // Pobierz zawartość odpowiedzi
        String responseContent = result.getResponse().getContentAsString();
        List<HouseResponse> houseResponses = objectMapper.readValue(responseContent, new TypeReference<>(){
        });
        List<String> houseResponsesNames = houseResponses.stream().map(HouseResponse::getHouseNumber).toList();

        //then
        assertFalse(houseResponses.isEmpty());
        assertEquals(1, houseResponses.size());
        assertEquals("House1",houseResponses.get(0).getHouseNumber());
        assertFalse(houseResponsesNames.contains("House2"));
    }
    @Test
    @DisplayName("Should return house with matching id")
    public void getHouseByPathVariableIdShouldReturnHouse() throws Exception {
        //given

        HouseInternalEntity house1 = new HouseInternalEntity(now, "House1", 3, 0);
        HouseInternalEntity house2 = new HouseInternalEntity(now, "House2", 2, 0);

        houseRepository.save(house1);
        houseRepository.save(house2);

        int id = house1.getId();
        //Hibernate starts indexing from 1! not 0.
        // ALSO: when the database was populated and cleared he continues id assignment!

        //when
        // Wykonaj żądanie HTTP GET na endpoint /houses
        MvcResult result = mockMvc
                .perform(get("/houses/{id}", id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        // Pobierz zawartość odpowiedzi
        String responseContent = result.getResponse().getContentAsString();
        HouseResponse houseResponse = objectMapper.readValue(responseContent, new TypeReference<>(){});

        //then
        assertEquals("House1", houseResponse.getHouseNumber());
        assertNotEquals("House2", houseResponse.getHouseNumber());
    }

    @Test
    @DisplayName("Should add house to empty database based on provided HouseRequest")
    public void addNewHouseShouldAddHouseToEmptyDatabase() throws Exception {
        //given
        HouseRequest houseRequest = new HouseRequest("house1", 3);
        String houseRequestJSONString = objectMapper.writeValueAsString(houseRequest);

        //when
        MvcResult result = mockMvc
                .perform(post("/houses").contentType(MediaType.APPLICATION_JSON)
                        .content(houseRequestJSONString))
                .andExpect(status().isCreated()).andReturn();

        //then
        Assertions.assertEquals(201, result.getResponse().getStatus());
    }

    @Test
    @DisplayName("Should add house to a populated database if no house matching HouseRequest exists")
    public void addNewHouseShouldAddHouseToDatabase() throws Exception {
        //given
        //populate database
        houseRepository.save(new HouseInternalEntity(now, "house0", 3, 0));
        houseRepository.save(new HouseInternalEntity(now, "house1", 3, 0));

        HouseRequest houseRequest = new HouseRequest("house2", 3);
        String houseRequestJSONString = objectMapper.writeValueAsString(houseRequest);

        //when
        MvcResult result = mockMvc
                .perform(post("/houses").contentType(MediaType.APPLICATION_JSON)
                        .content(houseRequestJSONString))
                .andExpect(status().isCreated()).andReturn();

        //then
        Assertions.assertEquals(201, result.getResponse().getStatus());
    }

    @Test
    @DisplayName("Should NOT add house to database which contains house with houseNumber equal to that from HouseRequest")
    public void addNewHouseShouldNotAddHouseToDatabase() throws Exception {
        //given
        //populate database
        houseRepository.save(new HouseInternalEntity(now, "house0", 3, 0));
        houseRepository.save(new HouseInternalEntity(now, "house1", 3, 0));

        HouseRequest houseRequest = new HouseRequest("house1", 3);
        String houseRequestJSONString = objectMapper.writeValueAsString(houseRequest);

        //when
        MvcResult result = mockMvc
                .perform(post("/houses").contentType(MediaType.APPLICATION_JSON)
                        .content(houseRequestJSONString))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        //then
        Assertions.assertEquals(422, result.getResponse().getStatus());
    }




}