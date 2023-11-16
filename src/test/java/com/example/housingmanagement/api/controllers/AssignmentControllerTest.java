package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.OccupantRepositoryJPA;
import com.example.housingmanagement.api.dbentities.Gender;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.AssignmentRequest;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.requests.OccupantRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    @DisplayName("Should assign an existing unassigned occupant to a house with spare capacity")
    void shouldAssignSpecificHomelessOccupantToSpecificHouseWithSpareCapacity() throws Exception {
        //given
        HouseInternalEntity house1 = new HouseInternalEntity(now, "house1", 3, 2);
        houseRepository.save(house1);

        OccupantInternalEntity occupant1 = new OccupantInternalEntity(now, "John", "Smith", Gender.MALE, null);
        occupantRepository.save(occupant1);

        HouseRequest houseRequest = new HouseRequest("house1");
        OccupantRequest occupantRequest = new OccupantRequest("John", "Smith", Gender.MALE);
        AssignmentRequest assignmentRequest = new AssignmentRequest(houseRequest, occupantRequest);

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/assign").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isOk()).andReturn();

        OccupantInternalEntity updatedOccupant = occupantRepository.findByFirstNameAndLastName(occupant1.getFirstName(), occupant1.getLastName());
        HouseInternalEntity updatedHouse = houseRepository.findByHouseNumber(house1.getHouseNumber());

        //then
        assertEquals(200, result.getResponse().getStatus());
        assertNotNull(updatedOccupant.getHouseInternalEntity());
        assertNotNull(updatedHouse);
        assertEquals(3, updatedHouse.getCurrentCapacity());
        assertEquals(updatedHouse.getHouseNumber(), updatedOccupant.getHouseInternalEntity().getHouseNumber());
    }

    @Test
    @DisplayName("Should NOT assign a unassigned existing occupant to a non-existing house")
    void shouldNotAssignUnassignedOccupantToNonExistingHouse() throws Exception {
        //given
        OccupantInternalEntity occupant1 = new OccupantInternalEntity(now, "John", "Smith", Gender.MALE, null);
        occupantRepository.save(occupant1);

        HouseRequest houseRequest = new HouseRequest("house1");
        OccupantRequest occupantRequest = new OccupantRequest("John", "Smith", Gender.MALE);
        AssignmentRequest assignmentRequest = new AssignmentRequest(houseRequest, occupantRequest);

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/assign").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertFalse(houseRepository.existsByHouseNumber("house1"));
        assertNull(occupant1.getHouseInternalEntity());
    }

    @Test
    @DisplayName("Should NOT assign a non-existing occupant to a house with spare capacity")
    void shouldNotAssignNonExistingOccupantToSpecificHouseWithSpareCapacity() throws Exception {
        //given
        HouseInternalEntity house1 = new HouseInternalEntity(now, "house1", 3, 2);
        houseRepository.save(house1);

        HouseRequest houseRequest = new HouseRequest("house1");
        OccupantRequest occupantRequest = new OccupantRequest("John", "Smith", Gender.MALE);
        AssignmentRequest assignmentRequest = new AssignmentRequest(houseRequest, occupantRequest);

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/assign").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        HouseInternalEntity updatedHouse = houseRepository.findByHouseNumber(house1.getHouseNumber());

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertNotNull(updatedHouse);
        assertEquals(2, updatedHouse.getCurrentCapacity());

    }

    @Test
    @DisplayName("Should NOT assign an unassigned occupant to a house with NO spare capacity")
    void shouldNotAssignSpecificHomelessOccupantToSpecificHouseWithNoSpareCapacity() throws Exception {
        //given
        HouseInternalEntity house1 = new HouseInternalEntity(now, "house1", 3, 3);
        houseRepository.save(house1);

        OccupantInternalEntity occupant1 = new OccupantInternalEntity(now, "John", "Smith", Gender.MALE, null);
        occupantRepository.save(occupant1);

        HouseRequest houseRequest = new HouseRequest("house1");
        OccupantRequest occupantRequest = new OccupantRequest("John", "Smith", Gender.MALE);
        AssignmentRequest assignmentRequest = new AssignmentRequest(houseRequest, occupantRequest);

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/assign").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        OccupantInternalEntity updatedOccupant = occupantRepository.findByFirstNameAndLastName(occupant1.getFirstName(), occupant1.getLastName());
        HouseInternalEntity updatedHouse = houseRepository.findByHouseNumber(house1.getHouseNumber());

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertNull(updatedOccupant.getHouseInternalEntity());
        assertNotNull(updatedHouse);
        assertEquals(3, updatedHouse.getCurrentCapacity());
    }

    @Test
    @DisplayName("Should NOT assign an already assigned occupant to a house with spare capacity")
    void shouldNotAssignSpecificAlreadyAssignedOccupantToSpecificHouseWithNoSpareCapacity() throws Exception {
        //given
        HouseInternalEntity house1 = new HouseInternalEntity(now, "house1", 3, 1);
        HouseInternalEntity house2 = new HouseInternalEntity(now, "house2", 3, 1);
        houseRepository.save(house1);
        houseRepository.save(house2);

        OccupantInternalEntity occupant1 = new OccupantInternalEntity(now, "John", "Smith", Gender.MALE, house2);
        occupantRepository.save(occupant1);

        HouseRequest houseRequest = new HouseRequest("house1");
        OccupantRequest occupantRequest = new OccupantRequest("John", "Smith", Gender.MALE);
        AssignmentRequest assignmentRequest = new AssignmentRequest(houseRequest, occupantRequest);

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/assign").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        OccupantInternalEntity updatedOccupant = occupantRepository.findByFirstNameAndLastName(occupant1.getFirstName(), occupant1.getLastName());
        HouseInternalEntity updatedHouse = houseRepository.findByHouseNumber(house1.getHouseNumber());

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertNotNull(updatedOccupant.getHouseInternalEntity());
        assertNotNull(updatedHouse);
        assertEquals(1, updatedHouse.getCurrentCapacity());
        assertNotEquals(updatedHouse.getHouseNumber(), updatedOccupant.getHouseInternalEntity().getHouseNumber());
    }

    @Test
    @DisplayName("Should NOT assign an unassigned occupant to a house with spare capacity if genders don't match")
    void shouldNotAssignHomelessOccupantToSpecificHouseWithSpareCapacityIfGendersDontMatch() throws Exception {
        //given
        HouseInternalEntity house1 = new HouseInternalEntity(now, "house1", 3, 1);
        houseRepository.save(house1);

        OccupantInternalEntity occupant1 = new OccupantInternalEntity(now, "John", "Smith", Gender.MALE, house1);
        occupantRepository.save(occupant1);

        HouseRequest houseRequest = new HouseRequest("house1");
        OccupantRequest occupantRequest = new OccupantRequest("Kate", "Miller", Gender.FEMALE);
        AssignmentRequest assignmentRequest = new AssignmentRequest(houseRequest, occupantRequest);

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/assign").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        HouseInternalEntity updatedHouse = houseRepository.findByHouseNumber(house1.getHouseNumber());
        List<OccupantInternalEntity> occupantInternalEntityList = occupantRepository.findAll();
        List<String> occupantInternalEntityFirstNames = occupantInternalEntityList.stream().map(OccupantInternalEntity::getFirstName)
                .toList();

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertFalse(occupantInternalEntityFirstNames.contains("Kate"));
        assertNotNull(updatedHouse);
        assertEquals(1, updatedHouse.getCurrentCapacity());
    }

    @Test
    @DisplayName("Should move an already assigned occupant to a different house if has spare capacity and genders match")
    void shouldMoveSpecificOccupantToDifferentHouse() throws Exception {

    }


}