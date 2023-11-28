package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.dbentities.Gender;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.AssignmentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.housingmanagement.api.testutils.DataBaseTestUtils.getFirstNames;
import static com.example.housingmanagement.api.testutils.DataBaseTestUtils.*;
import static com.example.housingmanagement.api.testutils.MappingTestUtils.*;
import static com.example.housingmanagement.api.testutils.WebUtils.getMvcResultOfPUT;
import static com.example.housingmanagement.api.testutils.WebUtils.performGet;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AssignmentControllerTest {

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        clearOccupantRepository();
        clearHouseRepository();
    }

    @Test
    @DisplayName("Should return a list of occupants of a given house")
    void shouldGetAllOccupantsOfSpecificHouse() throws Exception {
        //given

        HouseInternalEntity house1 = aPartiallyOccupiedHouse("house1", 3, 1);
        HouseInternalEntity house2 = aPartiallyOccupiedHouse("house2", 3, 1);

        putIntoHouseDatabase(house1, house2);

        OccupantInternalEntity occupant1 = new OccupantInternalEntity(now, "John", "Smith", Gender.MALE, house1);
        OccupantInternalEntity occupant2 = new OccupantInternalEntity(now, "Bret", "Miller", Gender.MALE, house1);

        putIntoOccupantDatabase(occupant1, occupant2);

        //when
        String responseContent = performGet(createValidHouseRequest("house1"), "/occupants_of_house");

        List<String> listOfFirstNames = getListOfFirstNames(responseContent);
        List<String> listOfLastNames = getListOfLastNames(responseContent);

        //then
        assertEquals(2, listOfFirstNames.size());
        assertEquals(2, listOfLastNames.size());
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
                new AssignmentRequest(
                        createValidHouseRequest("house1"),
                        createValidOccupantRequest("John", "Smith", Gender.MALE));

        //when
        var result = getMvcResultOfPUT(assignmentRequest, "/occupants/assign", status().isOk());

        OccupantInternalEntity updatedOccupant = getUpdatedOccupant("John", "Smith");
        HouseInternalEntity updatedHouse = getUpdatedHouse("house1");

        //then
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
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
                new AssignmentRequest(
                        createValidHouseRequest("house1"),
                        createValidOccupantRequest("John", "Smith", Gender.MALE));

        //when
        var result = getMvcResultOfPUT(assignmentRequest, "/occupants/assign", status().isUnprocessableEntity());

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus());
        assertFalse(isExistsByHouseNumber("house1"));
        assertNull(getOccupantByFirstNameAndLastName("John", "Smith").getHouseInternalEntity());
    }

    @Test
    @DisplayName("Should NOT assign a non-existing occupant to a house with spare capacity")
    void shouldNotAssignNonExistingOccupantToSpecificHouseWithSpareCapacity() throws Exception {
        //given
        putIntoHouseDatabase(aPartiallyOccupiedHouse("house1", 3, 2));

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(
                        createValidHouseRequest("house1"),
                        createValidOccupantRequest("John", "Smith", Gender.MALE));

        //when
        var result = getMvcResultOfPUT(assignmentRequest, "/occupants/assign", status().isUnprocessableEntity());

        HouseInternalEntity updatedHouse = getUpdatedHouse("house1");

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus());
        assertEquals(2, updatedHouse.getCurrentCapacity());
    }

    @Test
    @DisplayName("Should NOT assign an unassigned occupant to a house with NO spare capacity")
    void shouldNotAssignSpecificHomelessOccupantToSpecificHouseWithNoSpareCapacity() throws Exception {
        //given
        putIntoHouseDatabase(aPartiallyOccupiedHouse("house1", 3, 3));

        putIntoOccupantDatabase(maleOccupant("John", "Smith"));

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(
                        createValidHouseRequest("house1"),
                        createValidOccupantRequest("John", "Smith", Gender.MALE));

        //when
        var result = getMvcResultOfPUT(assignmentRequest, "/occupants/assign", status().isUnprocessableEntity());

        OccupantInternalEntity updatedOccupant = getUpdatedOccupant("John", "Smith");
        HouseInternalEntity updatedHouse = getUpdatedHouse("house1");

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus());
        assertNull(updatedOccupant.getHouseInternalEntity());
        assertEquals(3, updatedHouse.getCurrentCapacity());
    }

    @Test
    @DisplayName("Should NOT assign an already assigned occupant to a house with spare capacity")
    void shouldNotAssignSpecificAlreadyAssignedOccupantToSpecificHouseWithNoSpareCapacity() throws Exception {
        //given
        HouseInternalEntity house1 = aPartiallyOccupiedHouse("house1", 3, 1);
        HouseInternalEntity house2 = aPartiallyOccupiedHouse("house2", 3, 1);

        putIntoHouseDatabase(house1, house2);

        OccupantInternalEntity occupant1 = new OccupantInternalEntity(now, "John", "Smith", Gender.MALE, house2);

        putIntoOccupantDatabase(occupant1);

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(
                        createValidHouseRequest("house1"),
                        createValidOccupantRequest("John", "Smith", Gender.MALE));

        //when
        var result = getMvcResultOfPUT(assignmentRequest, "/occupants/assign", status().isUnprocessableEntity());

        OccupantInternalEntity updatedOccupant = getUpdatedOccupant(occupant1.getFirstName(), occupant1.getLastName());
        HouseInternalEntity updatedHouse = getUpdatedHouse(house1.getHouseNumber());

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus());
        assertNotNull(updatedOccupant.getHouseInternalEntity());
        assertEquals(1, updatedHouse.getCurrentCapacity());
        assertNotEquals(updatedHouse.getHouseNumber(), updatedOccupant.getHouseInternalEntity().getHouseNumber());
    }

    @Test
    @DisplayName("Should NOT assign an unassigned occupant to a house with spare capacity if genders don't match")
    void shouldNotAssignHomelessOccupantToSpecificHouseWithSpareCapacityIfGendersDontMatch() throws Exception {
        //given
        HouseInternalEntity house1 = aPartiallyOccupiedHouse("house1", 3, 1);

        putIntoHouseDatabase(house1);

        OccupantInternalEntity occupant1 = new OccupantInternalEntity(now, "John", "Smith", Gender.MALE, house1);

        putIntoOccupantDatabase(occupant1);

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(
                        createValidHouseRequest("house1"),
                        createValidOccupantRequest("Kate", "Miller", Gender.FEMALE));

        //when
        var result = getMvcResultOfPUT(assignmentRequest, "/occupants/assign", status().isUnprocessableEntity());

        HouseInternalEntity updatedHouse = getUpdatedHouse(house1.getHouseNumber());
        List<String> occupantInternalEntityFirstNames = getFirstNames();

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus());
        assertFalse(occupantInternalEntityFirstNames.contains("Kate"));
        assertEquals(1, updatedHouse.getCurrentCapacity());
    }


    @Test
    @DisplayName("Should move an already assigned occupant to a different house if has spare capacity and genders match")
    void shouldMoveSpecificOccupantToDifferentHouse() throws Exception {
        //given
        HouseInternalEntity sourceHouse = aPartiallyOccupiedHouse("sourceHouse", 3, 1);
        HouseInternalEntity targetHouse = aPartiallyOccupiedHouse("targetHouse", 3, 1);

        putIntoHouseDatabase(sourceHouse, targetHouse);

        OccupantInternalEntity occupantToMove = new OccupantInternalEntity(now, "Oliwia", "Ogieglo", Gender.FEMALE, sourceHouse);

        putIntoOccupantDatabase(occupantToMove);

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(
                        createValidHouseRequest("targetHouse"),
                        createValidOccupantRequest("Oliwia", "Ogieglo", Gender.FEMALE));

        //when
        var result = getMvcResultOfPUT(assignmentRequest, "/occupants/move", status().isOk());

        HouseInternalEntity updatedSourceHouse = getUpdatedHouse(sourceHouse.getHouseNumber());
        HouseInternalEntity updatedTargetHouse = getUpdatedHouse(targetHouse.getHouseNumber());

        //then
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(0, updatedSourceHouse.getCurrentCapacity());
        assertEquals(2, updatedTargetHouse.getCurrentCapacity());
    }

    @Test
    @DisplayName("Should NOT move an assigned occupant to a different house if house does not exist")
    void shouldNotMoveSpecificOccupantToNonExistingHouse() throws Exception {
        //given
        HouseInternalEntity sourceHouse = aPartiallyOccupiedHouse("sourceHouse", 3, 1);

        putIntoHouseDatabase(sourceHouse);

        OccupantInternalEntity occupantToMove = new OccupantInternalEntity(now, "Oliwia", "Ogieglo", Gender.FEMALE, sourceHouse);

        putIntoOccupantDatabase(occupantToMove);

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(
                        createValidHouseRequest("targetHouse"),
                        createValidOccupantRequest("Oliwia", "Ogieglo", Gender.FEMALE));

        //when
        var result = getMvcResultOfPUT(assignmentRequest, "/occupants/move", status().isUnprocessableEntity());

        HouseInternalEntity updatedSourceHouse = getUpdatedHouse(sourceHouse.getHouseNumber());
        OccupantInternalEntity updatedOccupant = getUpdatedOccupant(occupantToMove.getFirstName(), occupantToMove.getLastName());

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus());
        assertEquals(sourceHouse.getHouseNumber(), updatedOccupant.getHouseInternalEntity().getHouseNumber());
        assertEquals(1, updatedSourceHouse.getCurrentCapacity());
    }

    @Test
    @DisplayName("Should NOT move an assigned occupant to a different house if house does NOT have spare capacity but genders match")
    void shouldNotMoveSpecificOccupantToExistingHouseWithoutSpareCapacity() throws Exception {
        //given
        HouseInternalEntity sourceHouse = aPartiallyOccupiedHouse("sourceHouse", 3, 1);
        HouseInternalEntity targetHouse = aPartiallyOccupiedHouse("targetHouse", 3, 3);

        putIntoHouseDatabase(sourceHouse, targetHouse);

        OccupantInternalEntity occupantToMove = new OccupantInternalEntity(now, "Oliwia", "Ogieglo", Gender.FEMALE, sourceHouse);

        putIntoOccupantDatabase(occupantToMove);

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(
                        createValidHouseRequest("targetHouse"),
                        createValidOccupantRequest("Oliwia", "Ogieglo", Gender.FEMALE));

        //when
        var result = getMvcResultOfPUT(assignmentRequest, "/occupants/move", status().isUnprocessableEntity());

        HouseInternalEntity updatedSourceHouse = getUpdatedHouse(sourceHouse.getHouseNumber());
        HouseInternalEntity updatedTargetHouse = getUpdatedHouse(targetHouse.getHouseNumber());
        OccupantInternalEntity updatedOccupant = getUpdatedOccupant(occupantToMove.getFirstName(), occupantToMove.getLastName());

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus());
        assertEquals(sourceHouse.getHouseNumber(), updatedOccupant.getHouseInternalEntity().getHouseNumber());
        assertEquals(1, updatedSourceHouse.getCurrentCapacity());
        assertEquals(3, updatedTargetHouse.getCurrentCapacity());
    }

    @Test
    @DisplayName("Should NOT move an assigned occupant to a different house even though it has spare capacity but genders do NOT match")
    void shouldNotMoveSpecificOccupantToExistingHouseIfGendersDontMatch() throws Exception {
        //given
        HouseInternalEntity sourceHouse = aPartiallyOccupiedHouse("sourceHouse", 3, 1);
        HouseInternalEntity targetHouse = aPartiallyOccupiedHouse("targetHouse", 3, 1);

        putIntoHouseDatabase(sourceHouse, targetHouse);

        OccupantInternalEntity occupantToMove = new OccupantInternalEntity(now, "Oliwia", "Ogieglo", Gender.FEMALE, sourceHouse);
        OccupantInternalEntity occupantToStay = new OccupantInternalEntity(now, "Brian", "Greene", Gender.MALE, targetHouse);

        putIntoOccupantDatabase(occupantToMove, occupantToStay);

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(
                        createValidHouseRequest("targetHouse"),
                        createValidOccupantRequest("Oliwia", "Ogieglo", Gender.FEMALE));

        //when
        var result = getMvcResultOfPUT(assignmentRequest, "/occupants/move", status().isUnprocessableEntity());

        HouseInternalEntity updatedSourceHouse = getUpdatedHouse(sourceHouse.getHouseNumber());
        HouseInternalEntity updatedTargetHouse = getUpdatedHouse(targetHouse.getHouseNumber());
        OccupantInternalEntity updatedOccupantToMove = getUpdatedOccupant(occupantToMove.getFirstName(), occupantToMove.getLastName());
        OccupantInternalEntity updatedOccupantToStay = getUpdatedOccupant(occupantToStay.getFirstName(), occupantToStay.getLastName());

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus());
        assertEquals(sourceHouse.getHouseNumber(), updatedOccupantToMove.getHouseInternalEntity().getHouseNumber());
        assertEquals(targetHouse.getHouseNumber(), updatedOccupantToStay.getHouseInternalEntity().getHouseNumber());
        assertEquals(1, updatedSourceHouse.getCurrentCapacity());
        assertEquals(1, updatedTargetHouse.getCurrentCapacity());
    }

    @Test
    @DisplayName("Should NOT move an unassigned occupant to a different house even though it has spare capacity and genders match")
    void shouldNotMoveUnassignedSpecificOccupantToDifferentHouse() throws Exception {
        //given
        HouseInternalEntity house1 = aPartiallyOccupiedHouse("house1", 3, 1);
        HouseInternalEntity house2 = aPartiallyOccupiedHouse("house2", 3, 1);

        putIntoHouseDatabase(house1, house2);

        AssignmentRequest assignmentRequest =
                new AssignmentRequest(
                        createValidHouseRequest("targetHouse"),
                        createValidOccupantRequest("Oliwia", "Ogieglo", Gender.FEMALE));

        //when
        var result = getMvcResultOfPUT(assignmentRequest, "/occupants/move", status().isUnprocessableEntity());

        HouseInternalEntity updatedHouse1 = getUpdatedHouse(house1.getHouseNumber());
        HouseInternalEntity updatedHouse2 = getUpdatedHouse(house2.getHouseNumber());

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus());
        assertTrue(occupantRepositoryIsEmpty());
        assertEquals(1, updatedHouse1.getCurrentCapacity());
        assertEquals(1, updatedHouse2.getCurrentCapacity());
    }
}