package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.*;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.responses.OccupantResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class OccupantController {


    @Autowired
    private HousingDatabaseInterface housingDatabaseInterface;

    @GetMapping(value = "/occupants")
    public ResponseEntity<List<OccupantResponse>> getAllOccupants() {
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set("get", "get all Occupants from the Database");

        List<OccupantInternalEntity> occupantInternalEntityList = housingDatabaseInterface.getAllOccupants();
        List<OccupantResponse> occupantResponseList = toOccupantResponse(occupantInternalEntityList);

        return ResponseEntity.ok().headers(responseHeader).body(occupantResponseList);
    }

    @PostMapping(value = "/occupants/add_occupant_without_house")
    public ResponseEntity<String> addOccupantWithoutHouse(@RequestBody OccupantRequest occupantToBeAddedWithoutHouse) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //check if such Occupant already exists
        if (housingDatabaseInterface.existsByOccupant(occupantToBeAddedWithoutHouse)) {
            return ResponseEntity.badRequest().headers(responseHeaders).body(
                    "An Occupant with full name: " + occupantToBeAddedWithoutHouse.getFirstName()
                            + " " + occupantToBeAddedWithoutHouse.getLastName() + " already exists in the Database.");
        }
        //check if Gender is specified correctly
        if (!(housingDatabaseInterface.retrieveOccupantGenderFromRequest(occupantToBeAddedWithoutHouse).equals("M")
                ||
                housingDatabaseInterface.retrieveOccupantGenderFromRequest(occupantToBeAddedWithoutHouse).equals("F"))) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("You must specify Occupant gender as: " +
                    "M (male) or F (female)");
        }
        //add Occupant to Database
        housingDatabaseInterface.addOccupantToDatabase(toOccupantInternalEntity(occupantToBeAddedWithoutHouse));
        responseHeaders.set("post", "adding a new Occupant without a house to the Database");

        return ResponseEntity.ok().headers(responseHeaders).body("Added Occupant without a house: "
                + occupantToBeAddedWithoutHouse.getFirstName()
                + " " + occupantToBeAddedWithoutHouse.getLastName());
    }

    @DeleteMapping(value = "/occupants")
    public ResponseEntity<String> deleteSpecificOccupant(@RequestBody OccupantRequest occupantToBeDeleted) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //check if such occupant exists at all.
        if (!housingDatabaseInterface.existsByOccupant(occupantToBeDeleted)) {
            return ResponseEntity.badRequest().headers(responseHeaders).body(
                    "There is no Occupant: " + occupantToBeDeleted.getFirstName()
                            + occupantToBeDeleted.getLastName() + " in the Database.");
        }
        //check if occupant has a house
        if (housingDatabaseInterface.houseCurrentlyAssignedToThisOccupant(occupantToBeDeleted) != null) {
            //if so, identify where the Occupant lives and reduce current capacity by 1
            HouseInternalEntity houseWhereTheOccupantLives =
                    housingDatabaseInterface.houseCurrentlyAssignedToThisOccupant(occupantToBeDeleted);
            HouseRequest temporaryHouseRequest = new HouseRequest(houseWhereTheOccupantLives.getHouseNumber());
            housingDatabaseInterface.decreaseHouseCurrentCapacityByOne(temporaryHouseRequest);
        }
        housingDatabaseInterface.deleteOccupantFromDatabase(occupantToBeDeleted);

        return ResponseEntity.ok().headers(responseHeaders).body("Occupant: "
                + occupantToBeDeleted.getFirstName() + " "
                + occupantToBeDeleted.getLastName() + " was deleted from Database.");
    }


    private List<OccupantResponse> toOccupantResponse(List<OccupantInternalEntity> occupantInternalEntityList) {
        List<OccupantResponse> occupantResponseList = new ArrayList<>();
        for (int i = 0; i < occupantInternalEntityList.size(); i++) {
            OccupantInternalEntity currentElement = occupantInternalEntityList.get(i);
            OccupantResponse occupantResponse = new OccupantResponse(currentElement.getFirstName(), currentElement.getLastName());
            occupantResponseList.add(occupantResponse);
        }
        return occupantResponseList;
    }

    private OccupantInternalEntity toOccupantInternalEntity(OccupantRequest occupantToBeAddedWithoutHouse) {
        return new OccupantInternalEntity(occupantToBeAddedWithoutHouse.getFirstName(), occupantToBeAddedWithoutHouse.getLastName()
                , occupantToBeAddedWithoutHouse.getGender());
    }


}
