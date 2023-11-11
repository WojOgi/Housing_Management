package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.responses.HouseResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

}