package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.OccupantRepositoryJPA;
import com.example.housingmanagement.api.dbentities.Gender;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.responses.OccupantResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OccupantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OccupantRepositoryJPA occupantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        occupantRepository.deleteAll();
    }

    @Test
    @DisplayName("getAllOccupants should return a list of occupants as List<OccupantResponse>")
    public void getAllOccupantsShouldReturnListOfOccupants() throws Exception {
        //given
        putIntoOccupantDatabase(maleOccupant("John", "Smith"));
        putIntoOccupantDatabase(femaleOccupant("Sarah", "Brent"));

        //when
        MvcResult result = mockMvc.perform(get("/occupants").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        String responseContent = result.getResponse().getContentAsString();
        List<OccupantResponse> occupantResponses = objectMapper.readValue(responseContent, new TypeReference<>() {
        });
        List<String> firstNames = occupantResponses.stream().map(OccupantResponse::getFirstName).toList();
        List<String> lastNames = occupantResponses.stream().map(OccupantResponse::getLastName).toList();

        //then
        assertFalse(occupantResponses.isEmpty());
        assertEquals(2, occupantResponses.size());
        assertEquals("John", firstNames.get(0));
        assertEquals("Sarah", firstNames.get(1));
        assertEquals("Smith", lastNames.get(0));
        assertEquals("Brent", lastNames.get(1));
    }

    @Test
    @DisplayName("Should return occupant with matching id")
    public void getOccupantByPathVariableIdShouldReturnOccupant() throws Exception {
        //given
        putIntoOccupantDatabase(maleOccupant("John", "Smith"));
        putIntoOccupantDatabase(femaleOccupant("Sarah", "Brent"));

        int id = occupantRepository.findByFirstNameAndLastName("John", "Smith").getId();

        //when
        MvcResult result = mockMvc.perform(get("/occupants/{id}", id).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OccupantResponse occupantResponse = objectMapper.readValue(responseContent, new TypeReference<>() {
        });

        //then
        assertEquals("John", occupantResponse.getFirstName());
        assertEquals("Smith", occupantResponse.getLastName());
        assertNotEquals("Sarah", occupantResponse.getFirstName());
        assertNotEquals("Brent", occupantResponse.getLastName());
    }

    @Test
    @DisplayName("Should add occupant to empty database when gender is specified correctly")
    public void addOccupantWithoutHouseIfOccupantDoesNotExistAndGenderOK() throws Exception {
        //given
        String occupantRequestJSONString = objectMapper.writeValueAsString(createValidOccupantRequest("Barry", "White", Gender.MALE));;
        //when
        MvcResult result = mockMvc.perform(post("/occupants").contentType(MediaType.APPLICATION_JSON).content(occupantRequestJSONString))
                .andExpect(status().isCreated()).andReturn();
        //then
        Assertions.assertEquals(201, result.getResponse().getStatus());
        assertEquals(occupantRepository.findAll().get(0).getFirstName(), "Barry");
        assertEquals(occupantRepository.findAll().get(0).getLastName(), "White");
        assertEquals(occupantRepository.findAll().get(0).getGender(), Gender.MALE);
        assertEquals(1, occupantRepository.findAll().size());
    }

    @Test
    @DisplayName("Should NOT add occupant if such occupant exists (first AND last name match) and gender is specified correctly")
    public void shouldNotAddOccupantWithoutHouseIfOccupantDoesNotExistAndGenderOK() throws Exception {
        //given
        putIntoOccupantDatabase(maleOccupant("Barry", "White"));

        String occupantRequestJSONString = objectMapper
                .writeValueAsString(createValidOccupantRequest("Barry", "White", Gender.MALE));
        //when
        MvcResult result = mockMvc.perform(post("/occupants").contentType(MediaType.APPLICATION_JSON).content(occupantRequestJSONString))
                .andExpect(status().isUnprocessableEntity()).andReturn();
        //then
        Assertions.assertEquals(422, result.getResponse().getStatus());
        assertEquals(1, occupantRepository.findAll().size());
    }

    @Test
    @DisplayName("Should NOT add occupant to empty database when gender is NOT specified correctly")
    public void shouldNotAddOccupantWithoutHouseIfGenderNotOK() throws Exception {
        //given
        String occupantRequestJSONString = objectMapper
                .writeValueAsString(createValidOccupantRequest("Barry", "White", Gender.UNICORN));
        //when
        MvcResult result = mockMvc.perform(post("/occupants").contentType(MediaType.APPLICATION_JSON).content(occupantRequestJSONString))
                .andExpect(status().isUnprocessableEntity()).andReturn();
        //then
        Assertions.assertEquals(422, result.getResponse().getStatus());
        assertTrue(occupantRepository.findAll().isEmpty());
    }

    private OccupantInternalEntity maleOccupant(String firstName, String lastName) {
        return new OccupantInternalEntity(now, firstName, lastName, Gender.MALE);
    }

    private OccupantInternalEntity femaleOccupant (String firstName, String lastName) {
        return new OccupantInternalEntity(now, firstName, lastName, Gender.FEMALE);
    }
    private static OccupantRequest createValidOccupantRequest(String firstName, String lastName, Gender gender) {
        return new OccupantRequest(firstName, lastName, gender);
    }

    private void putIntoOccupantDatabase(OccupantInternalEntity occupantInternalEntity){
        occupantRepository.save(occupantInternalEntity);
    }


}