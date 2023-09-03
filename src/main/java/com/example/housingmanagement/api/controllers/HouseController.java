package com.example.housingmanagement.api.controllers;


import com.example.housingmanagement.api.*;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.responses.HouseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class HouseController {

    @Autowired
    private HousingDatabaseInterface housingDatabaseInterface;

    @GetMapping(value = "/housing")
    public ResponseEntity<List<HouseResponse>> getAllHouses() {
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set("get", "get all Houses from the Database");

        List<HouseInternalEntity> allHouseInternalEntities = housingDatabaseInterface.getAllHouses();
        List<HouseResponse> houseResponses = toHouseResponse(allHouseInternalEntities);

        return ResponseEntity.ok().headers(responseHeader).body(houseResponses);
    }

    @GetMapping(value = "occupants_of_a_specific_house")
    public ResponseEntity<String> getAllOccupantsOfSpecificHouse(@RequestBody HouseRequest houseRequest) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //checks if this house exists
        if (!housingDatabaseInterface.existsByHouse(houseRequest)) {
            return ResponseEntity.badRequest().headers(responseHeaders).body(
                    "There is no house with number " + houseRequest.getHouseNumber() + " in the Database.");
        }
        //identifies house in internal database that matches the house from request
        Optional<HouseInternalEntity> houseInternalEntity =
                Optional.ofNullable(housingDatabaseInterface.identifyHouseInternalEntity(houseRequest));
        //returns a list of occupants of this house
        List<OccupantInternalEntity> occupantsAssignedToThisHouse =
                housingDatabaseInterface.getOccupantsAssignedToThisHouseIntEnt(houseInternalEntity);

        return ResponseEntity.ok().headers(responseHeaders).body(occupantsAssignedToThisHouse.toString());

    }

    @PostMapping(value = "/housing")
    public ResponseEntity<String> addNewHouse(@RequestBody HouseRequest houseToBeAdded) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //checks if this house exists
        if (housingDatabaseInterface.existsByHouse(houseToBeAdded)) {
            return ResponseEntity.badRequest().headers(responseHeaders).body(
                    "A House with number " + houseToBeAdded.getHouseNumber() + " already exists");
        }
        housingDatabaseInterface.addHouseToDatabase(toHouseInternalEntity(houseToBeAdded));
        responseHeaders.set("post", "adding a new House to the Database");

        return ResponseEntity.ok().headers(responseHeaders).body("Added House with address: "
                + houseToBeAdded.getHouseNumber());
    }

    @DeleteMapping(value = "/housing")
    public ResponseEntity<String> deleteSpecificHouse(@RequestBody HouseRequest houseToBeDeleted) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //check if such house exists in Database
        if (!housingDatabaseInterface.existsByHouse(houseToBeDeleted)) {
            return ResponseEntity.badRequest().headers(responseHeaders).body(
                    "There is no house with number " + houseToBeDeleted.getHouseNumber() + " in the Database.");
        }
        //check if the House is occupied by anybody
        if (housingDatabaseInterface.houseCurrentCapacity(houseToBeDeleted) > 0) {
            return ResponseEntity.badRequest().headers(responseHeaders).body(
                    "This house is occupied. House needs to be vacated before removal from the Database.");
        }
        //TODO: the method below fails for some reason
        housingDatabaseInterface.deleteHouseFromDatabase(houseToBeDeleted);

        return ResponseEntity.ok().headers(responseHeaders).body("House with address: "
                + houseToBeDeleted.getHouseNumber() + " was deleted from Database.");
    }


    private List<HouseResponse> toHouseResponse(List<HouseInternalEntity> allHouseInternalEntities) {
        List<HouseResponse> houseResponseList = new ArrayList<>();
        for (int i = 0; i < allHouseInternalEntities.size(); i++) {
            HouseInternalEntity currentElement = allHouseInternalEntities.get(i);
            HouseResponse houseResponse = new HouseResponse(currentElement.getHouseNumber());
            houseResponseList.add(houseResponse);
        }
        return houseResponseList;

    }

    private HouseInternalEntity toHouseInternalEntity(HouseRequest houseToBeAdded) {
        return new HouseInternalEntity(houseToBeAdded.getHouseNumber(),
                houseToBeAdded.getMaxCapacity(), 0);
    }


}
