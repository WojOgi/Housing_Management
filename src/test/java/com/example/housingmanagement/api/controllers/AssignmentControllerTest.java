package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.OccupantRepositoryJPA;
import com.example.housingmanagement.api.dbentities.Gender;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.responses.OccupantResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OccupantRepositoryJPA occupantRepository;

    @Autowired
    private HouseRepositoryJPA houseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        occupantRepository.deleteAll();
        houseRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return a list of occupants of a given house")
    void shouldGetAllOccupantsOfSpecificHouse() throws Exception {
        //given
        HouseInternalEntity house1 = new HouseInternalEntity(now, "house1", 3, 2);
        HouseInternalEntity house2 = new HouseInternalEntity(now, "house2", 3, 1);
        houseRepository.save(house1);
        houseRepository.save(house2);

        OccupantInternalEntity occupant1 = new OccupantInternalEntity(now, "John", "Smith", Gender.MALE, house1);
        OccupantInternalEntity occupant2 = new OccupantInternalEntity(now, "Bret", "Miller", Gender.MALE, house1);
        occupantRepository.save(occupant1);
        occupantRepository.save(occupant2);

        HouseRequest houseRequest = new HouseRequest("house1");
        String houseRequestJSON = objectMapper.writeValueAsString(houseRequest);

        //when
        MvcResult result = mockMvc.perform(get("/occupants_of_house").contentType(MediaType.APPLICATION_JSON).content(houseRequestJSON))
                .andExpect(status().isOk()).andReturn();

        String responseContent = result.getResponse().getContentAsString();

        List<OccupantResponse> occupantResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
        });
        List<String> listOfFirstNames = occupantResponses.stream().map(OccupantResponse::getFirstName).toList();
        List<String> listOfLastNames = occupantResponses.stream().map(OccupantResponse::getLastName).toList();

        //then
        assertEquals(2, occupantResponses.size());
        assertTrue(listOfFirstNames.contains("John") && listOfFirstNames.contains("Bret"));
        assertTrue(listOfLastNames.contains("Smith") && listOfLastNames.contains("Miller"));
    }




}