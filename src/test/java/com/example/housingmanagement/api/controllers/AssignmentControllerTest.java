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
        putIntoHouseDatabase(aPartiallyOccupiedHouse("house1", 3, 1));
        putIntoHouseDatabase(aPartiallyOccupiedHouse("house2", 3, 1));

        OccupantInternalEntity occupant1 = new OccupantInternalEntity(now, "John", "Smith", Gender.MALE, houseRepository.findByHouseNumber("house1"));
        OccupantInternalEntity occupant2 = new OccupantInternalEntity(now, "Bret", "Miller", Gender.MALE, houseRepository.findByHouseNumber("house1"));
        occupantRepository.save(occupant1);
        occupantRepository.save(occupant2);

        String houseRequestJSON = objectMapper.writeValueAsString(createValidHouseRequest("house1"));

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
        putIntoHouseDatabase(aPartiallyOccupiedHouse("house1", 3, 2));

        putIntoOccupantDatabase(maleOccupant("John", "Smith"));

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(createValidHouseRequest("house1"), createValidOccupantRequest("John", "Smith", Gender.MALE));

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/assign").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isOk()).andReturn();

        OccupantInternalEntity updatedOccupant = occupantRepository.findByFirstNameAndLastName("John", "Smith");
        HouseInternalEntity updatedHouse = houseRepository.findByHouseNumber("house1");

        //then
        assertEquals(200, result.getResponse().getStatus());
        assertNotNull(updatedOccupant.getHouseInternalEntity());
        assertEquals(3, updatedHouse.getCurrentCapacity());
        assertEquals(updatedHouse.getHouseNumber(), updatedOccupant.getHouseInternalEntity().getHouseNumber());
    }

    @Test
    @DisplayName("Should NOT assign an unassigned existing occupant to a non-existing house")
    void shouldNotAssignUnassignedOccupantToNonExistingHouse() throws Exception {
        //given
        putIntoOccupantDatabase(maleOccupant("John", "Smith"));

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(createValidHouseRequest("house1"), createValidOccupantRequest("John", "Smith", Gender.MALE));

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/assign").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertFalse(houseRepository.existsByHouseNumber("house1"));
        assertNull(occupantRepository.findByFirstNameAndLastName("John", "Smith").getHouseInternalEntity());
    }

    @Test
    @DisplayName("Should NOT assign a non-existing occupant to a house with spare capacity")
    void shouldNotAssignNonExistingOccupantToSpecificHouseWithSpareCapacity() throws Exception {
        //given
        putIntoHouseDatabase(aPartiallyOccupiedHouse("house1", 3, 2));

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(createValidHouseRequest("house1"), createValidOccupantRequest("John", "Smith", Gender.MALE));

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/assign").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        HouseInternalEntity updatedHouse = houseRepository.findByHouseNumber("house1");

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertEquals(2, updatedHouse.getCurrentCapacity());
    }

    @Test
    @DisplayName("Should NOT assign an unassigned occupant to a house with NO spare capacity")
    void shouldNotAssignSpecificHomelessOccupantToSpecificHouseWithNoSpareCapacity() throws Exception {
        //given
        putIntoHouseDatabase(aPartiallyOccupiedHouse("house1", 3, 3));

        putIntoOccupantDatabase(maleOccupant("John", "Smith"));

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(createValidHouseRequest("house1"), createValidOccupantRequest("John", "Smith", Gender.MALE));

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/assign").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        OccupantInternalEntity updatedOccupant = occupantRepository.findByFirstNameAndLastName("John", "Smith");
        HouseInternalEntity updatedHouse = houseRepository.findByHouseNumber("house1");

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertNull(updatedOccupant.getHouseInternalEntity());
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

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(createValidHouseRequest("house1"), createValidOccupantRequest("John", "Smith", Gender.MALE));

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/assign").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        OccupantInternalEntity updatedOccupant = occupantRepository.findByFirstNameAndLastName(occupant1.getFirstName(), occupant1.getLastName());
        HouseInternalEntity updatedHouse = houseRepository.findByHouseNumber(house1.getHouseNumber());

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertNotNull(updatedOccupant.getHouseInternalEntity());
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

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(createValidHouseRequest("house1"), createValidOccupantRequest("Kate", "Miller", Gender.FEMALE));

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
        assertEquals(1, updatedHouse.getCurrentCapacity());
    }

    @Test
    @DisplayName("Should move an already assigned occupant to a different house if has spare capacity and genders match")
    void shouldMoveSpecificOccupantToDifferentHouse() throws Exception {
        //given
        HouseInternalEntity sourceHouse = new HouseInternalEntity(now, "sourceHouse", 3, 1);
        HouseInternalEntity targetHouse = new HouseInternalEntity(now, "targetHouse", 3, 1);
        houseRepository.save(sourceHouse);
        houseRepository.save(targetHouse);

        OccupantInternalEntity occupantToMove = new OccupantInternalEntity(now, "Oliwia", "Ogieglo", Gender.FEMALE, sourceHouse);
        occupantRepository.save(occupantToMove);

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(createValidHouseRequest("targetHouse"), createValidOccupantRequest("Oliwia", "Ogieglo", Gender.FEMALE));

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/move").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isOk()).andReturn();

        HouseInternalEntity updatedSourceHouse = houseRepository.findByHouseNumber(sourceHouse.getHouseNumber());
        HouseInternalEntity updatedTargetHouse = houseRepository.findByHouseNumber(targetHouse.getHouseNumber());

        //then
        assertEquals(200, result.getResponse().getStatus());
        assertEquals(0, updatedSourceHouse.getCurrentCapacity());
        assertEquals(2, updatedTargetHouse.getCurrentCapacity());
    }

    @Test
    @DisplayName("Should NOT move an assigned occupant to a different house if house does not exist")
    void shouldNotMoveSpecificOccupantToNonExistingHouse() throws Exception {
        //given
        HouseInternalEntity sourceHouse = new HouseInternalEntity(now, "sourceHouse", 3, 1);
        houseRepository.save(sourceHouse);

        OccupantInternalEntity occupantToMove = new OccupantInternalEntity(now, "Oliwia", "Ogieglo", Gender.FEMALE, sourceHouse);
        occupantRepository.save(occupantToMove);

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(createValidHouseRequest("targetHouse"), createValidOccupantRequest("Oliwia", "Ogieglo", Gender.FEMALE));

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/move").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        HouseInternalEntity updatedSourceHouse = houseRepository.findByHouseNumber(sourceHouse.getHouseNumber());
        OccupantInternalEntity updatedOccupant = occupantRepository.findByFirstNameAndLastName(occupantToMove.getFirstName(), occupantToMove.getLastName());

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertEquals(sourceHouse.getHouseNumber(), updatedOccupant.getHouseInternalEntity().getHouseNumber());
        assertEquals(1, updatedSourceHouse.getCurrentCapacity());
    }

    @Test
    @DisplayName("Should NOT move an assigned occupant to a different house if house does NOT have spare capacity but genders match")
    void shouldNotMoveSpecificOccupantToExistingHouseWithoutSpareCapacity() throws Exception {
        //given
        HouseInternalEntity sourceHouse = new HouseInternalEntity(now, "sourceHouse", 3, 1);
        HouseInternalEntity targetHouse = new HouseInternalEntity(now, "targetHouse", 3, 3);
        houseRepository.save(sourceHouse);
        houseRepository.save(targetHouse);

        OccupantInternalEntity occupantToMove = new OccupantInternalEntity(now, "Oliwia", "Ogieglo", Gender.FEMALE, sourceHouse);
        occupantRepository.save(occupantToMove);

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(createValidHouseRequest("targetHouse"), createValidOccupantRequest("Oliwia", "Ogieglo", Gender.FEMALE));

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/move").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        HouseInternalEntity updatedSourceHouse = houseRepository.findByHouseNumber(sourceHouse.getHouseNumber());
        HouseInternalEntity updatedTargetHouse = houseRepository.findByHouseNumber(targetHouse.getHouseNumber());
        OccupantInternalEntity updatedOccupant = occupantRepository.findByFirstNameAndLastName(occupantToMove.getFirstName(), occupantToMove.getLastName());

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertEquals(sourceHouse.getHouseNumber(), updatedOccupant.getHouseInternalEntity().getHouseNumber());
        assertEquals(1, updatedSourceHouse.getCurrentCapacity());
        assertEquals(3, updatedTargetHouse.getCurrentCapacity());
    }

    @Test
    @DisplayName("Should NOT move an assigned occupant to a different house even though it has spare capacity but genders do NOT match")
    void shouldNotMoveSpecificOccupantToExistingHouseIfGendersDontMatch() throws Exception {
        //given
        HouseInternalEntity sourceHouse = new HouseInternalEntity(now, "sourceHouse", 3, 1);
        HouseInternalEntity targetHouse = new HouseInternalEntity(now, "targetHouse", 3, 1);
        houseRepository.save(sourceHouse);
        houseRepository.save(targetHouse);

        OccupantInternalEntity occupantToMove = new OccupantInternalEntity(now, "Oliwia", "Ogieglo", Gender.FEMALE, sourceHouse);
        OccupantInternalEntity occupantToStay = new OccupantInternalEntity(now, "Brian", "Greene", Gender.MALE, targetHouse);
        occupantRepository.save(occupantToMove);
        occupantRepository.save(occupantToStay);

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(createValidHouseRequest("targetHouse"), createValidOccupantRequest("Oliwia", "Ogieglo", Gender.FEMALE));

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/move").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        HouseInternalEntity updatedSourceHouse = houseRepository.findByHouseNumber(sourceHouse.getHouseNumber());
        HouseInternalEntity updatedTargetHouse = houseRepository.findByHouseNumber(targetHouse.getHouseNumber());
        OccupantInternalEntity updatedOccupantToMove = occupantRepository.findByFirstNameAndLastName(occupantToMove.getFirstName(), occupantToMove.getLastName());
        OccupantInternalEntity updatedOccupantToStay = occupantRepository.findByFirstNameAndLastName(occupantToStay.getFirstName(), occupantToStay.getLastName());

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertEquals(sourceHouse.getHouseNumber(), updatedOccupantToMove.getHouseInternalEntity().getHouseNumber());
        assertEquals(targetHouse.getHouseNumber(), updatedOccupantToStay.getHouseInternalEntity().getHouseNumber());
        assertEquals(1, updatedSourceHouse.getCurrentCapacity());
        assertEquals(1, updatedTargetHouse.getCurrentCapacity());
    }

    @Test
    @DisplayName("Should NOT move an unassigned occupant to a different house even though it has spare capacity and genders match")
    void shouldNotMoveUnassignedSpecificOccupantToDifferentHouse() throws Exception {
        //given
        HouseInternalEntity house1 = new HouseInternalEntity(now, "house1", 3, 1);
        HouseInternalEntity house2 = new HouseInternalEntity(now, "house2", 3, 1);
        houseRepository.save(house1);
        houseRepository.save(house2);

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(createValidHouseRequest("targetHouse"), createValidOccupantRequest("Oliwia", "Ogieglo", Gender.FEMALE));

        String assignmentRequestJSON = objectMapper.writeValueAsString(assignmentRequest);

        //when
        MvcResult result = mockMvc.perform(put("/occupants/move").contentType(MediaType.APPLICATION_JSON).content(assignmentRequestJSON))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        HouseInternalEntity updatedHouse1 = houseRepository.findByHouseNumber(house1.getHouseNumber());
        HouseInternalEntity updatedHouse2 = houseRepository.findByHouseNumber(house2.getHouseNumber());

        //then
        assertEquals(422, result.getResponse().getStatus());
        assertTrue(occupantRepository.findAll().isEmpty());
        assertEquals(1, updatedHouse1.getCurrentCapacity());
        assertEquals(1, updatedHouse2.getCurrentCapacity());
    }

    private static HouseRequest createValidHouseRequest(String houseNumber) {
        return new HouseRequest(houseNumber);
    }

    private static OccupantRequest createValidOccupantRequest(String firstName, String lastName, Gender gender) {
        return new OccupantRequest(firstName, lastName, gender);
    }

    private HouseInternalEntity aPartiallyOccupiedHouse(String houseNumber, int maxCapacity, int currentCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, currentCapacity);
    }

    private void putIntoHouseDatabase(HouseInternalEntity houseInternalEntity) {
        houseRepository.save(houseInternalEntity);
    }
    private void putIntoOccupantDatabase(OccupantInternalEntity occupantInternalEntity){
        occupantRepository.save(occupantInternalEntity);
    }
    private OccupantInternalEntity maleOccupant(String firstName, String lastName) {
        return new OccupantInternalEntity(now, firstName, lastName, Gender.MALE);
    }

}