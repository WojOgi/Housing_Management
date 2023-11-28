package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.dbentities.Gender;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.responses.OccupantResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.example.housingmanagement.api.testutils.DataBaseTestUtils.*;
import static com.example.housingmanagement.api.testutils.ResponseMapperTestUtils.getFirstNames;
import static com.example.housingmanagement.api.testutils.ResponseMapperTestUtils.*;
import static com.example.housingmanagement.api.testutils.EntityAndRequestCreatorTestUtils.*;
import static com.example.housingmanagement.api.testutils.WebUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OccupantControllerTest {

    @BeforeEach
    void setUp() {
        clearOccupantRepository();
    }

    @Test
    @DisplayName("getAllOccupants should return a list of occupants as List<OccupantResponse>")
    public void getAllOccupantsShouldReturnListOfOccupants() throws Exception {
        //given
        putIntoOccupantDatabase(maleOccupant("John", "Smith"));
        putIntoOccupantDatabase(femaleOccupant("Sarah", "Brent"));

        //when
        List<OccupantResponse> occupantResponses = getOccupantResponseList(performGet("/occupants"));

        List<String> firstNames = getFirstNames(occupantResponses);
        List<String> lastNames = getLastNames(occupantResponses);

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

        int id = getIdByFirstAndLastName("John", "Smith");

        //when
        OccupantResponse occupantResponse = getOccupantResponse(performGetWithId("/occupants/{id}", id));

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
        OccupantRequest occupantRequest = createValidOccupantRequest("Barry", "White", Gender.MALE);

        //when
        var result = getMvcResultOfPOST(occupantRequest, "/occupants", status().isCreated());

        //then
        Assertions.assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertEquals(getFirstNameByIndex(0), "Barry");
        assertEquals(getLastNameByIndex(0), "White");
        assertEquals(getGenderByIndex(0), Gender.MALE);
        assertEquals(1, getOccupantRepositorySize());
    }

    @Test
    @DisplayName("Should NOT add occupant if such occupant exists (first AND last name match) and gender is specified correctly")
    public void shouldNotAddOccupantWithoutHouseIfOccupantDoesNotExistAndGenderOK() throws Exception {
        //given
        putIntoOccupantDatabase(maleOccupant("Barry", "White"));

        OccupantRequest occupantRequest = createValidOccupantRequest("Barry", "White", Gender.MALE);

        //when
        var result = getMvcResultOfPOST(occupantRequest, "/occupants", status().isUnprocessableEntity());

        //then
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus());
        assertEquals(1, getOccupantRepositorySize());
    }

    @Test
    @DisplayName("Should NOT add occupant to empty database when gender is NOT specified correctly")
    public void shouldNotAddOccupantWithoutHouseIfGenderNotOK() throws Exception {
        //given
        OccupantRequest occupantRequest = createValidOccupantRequest("Barry", "White", Gender.UNICORN);

        //when
        var result = getMvcResultOfPOST(occupantRequest, "/occupants", status().isUnprocessableEntity());

        //then
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus());
        assertTrue(occupantRepositoryIsEmpty());
    }


}