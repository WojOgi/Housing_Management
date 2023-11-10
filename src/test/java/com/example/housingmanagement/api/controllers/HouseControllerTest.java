package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.responses.HouseResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Test
    public void getAllHousesShouldReturnListOfHouses() throws Exception {
        houseRepository.save(new HouseInternalEntity(LocalDateTime.now(), "11111", 3, 0));

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

        // Dodaj swoje asercje związane z oczekiwanym wynikiem
        assertEquals(6, houseResponses.size());
    }

}