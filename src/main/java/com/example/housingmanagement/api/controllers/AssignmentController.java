package com.example.housingmanagement.api.controllers;


import com.example.housingmanagement.api.*;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.AssignmentRequest;
import com.example.housingmanagement.api.requests.HouseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AssignmentController {
    @Autowired
    private HousingDatabaseInterface housingDatabaseInterface;

    @PutMapping(value = "/occupants/assign_specific_homeless_occupant_to_specific_house")
    public ResponseEntity<String> assignSpecificHomelessOccupantToSpecificHouse(@RequestBody AssignmentRequest assignmentRequest) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //check if target house exists
        if (!housingDatabaseInterface.existsByHouse(assignmentRequest.getHouseToAssign())) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("There is no house with address: "
                    + assignmentRequest.getHouseToAssign().getHouseNumber() + " in the database.");
        }
        //check if target occupant exists
        if (!housingDatabaseInterface.existsByOccupant(assignmentRequest.getOccupantToAssign())) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("There is no Occupant named: "
                    + assignmentRequest.getOccupantToAssign().getFirstName() + " "
                    + assignmentRequest.getOccupantToAssign().getLastName()
                    + " in the database.");
        }
        //check if house has spare capacity
        if (housingDatabaseInterface.houseHasSpareCapacity(assignmentRequest.getHouseToAssign())) {
            //check if the occupant already had a house before
            if (housingDatabaseInterface.
                    houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign()) != null) {
                return ResponseEntity.badRequest().headers(responseHeaders).body("Occupant: "
                        + assignmentRequest.getOccupantToAssign().getFirstName() + " "
                        + assignmentRequest.getOccupantToAssign().getLastName() + " "
                        + "already has a House: " + housingDatabaseInterface
                        .houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign()).toString());
            }
            //TODO: check against gender mixing
            //assign the occupant from request to the house from request
            housingDatabaseInterface.assignSpecificOccupantToSpecificHouse(assignmentRequest.getHouseToAssign(), assignmentRequest.getOccupantToAssign());
            //increase the capacity of the house from request by one
            housingDatabaseInterface.increaseHouseCurrentCapacityByOne(assignmentRequest.getHouseToAssign());

            return ResponseEntity.ok().headers(responseHeaders).body("Occupant: "
                    + assignmentRequest.getOccupantToAssign().getFirstName() + " "
                    + assignmentRequest.getOccupantToAssign().getLastName() + " "
                    + "was assigned to: " + assignmentRequest.getHouseToAssign().getHouseNumber());
        }
        return ResponseEntity.badRequest().headers(responseHeaders).body("The requested house: "
                + assignmentRequest.getHouseToAssign().getHouseNumber() + " does not have spare capacity.");
    }

    @PutMapping(value = "/occupants/move_occupant_to_different_house")
    public ResponseEntity<String> moveSpecificOccupantToDifferentHouse(@RequestBody AssignmentRequest assignmentRequest) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //check if target house exists
        if (!housingDatabaseInterface.existsByHouse(assignmentRequest.getHouseToAssign())) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("There is no house with address: "
                    + assignmentRequest.getHouseToAssign().getHouseNumber() + " in the database.");
        }
        //check if target occupant exists
        if (!housingDatabaseInterface.existsByOccupant(assignmentRequest.getOccupantToAssign())) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("There is no Occupant named: "
                    + assignmentRequest.getOccupantToAssign().getFirstName() + " "
                    + assignmentRequest.getOccupantToAssign().getLastName()
                    + " in the database.");
        }
        //check if target occupant has a house
        if (housingDatabaseInterface.houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign()) == null) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("Occupant: "
                    + assignmentRequest.getOccupantToAssign().getFirstName() + " "
                    + assignmentRequest.getOccupantToAssign().getLastName() + " "
                    + " does not have a House assigned yet. Please issue a PUT request to /occupants/assign_specific_homeless_occupant_to_specific_house).");
        }
        //check if target house is the same as old house
        if (housingDatabaseInterface.houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign()).toString()
                .equals(assignmentRequest.getHouseToAssign().toString())) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("Occupant: "
                    + assignmentRequest.getOccupantToAssign().getFirstName() + " "
                    + assignmentRequest.getOccupantToAssign().getLastName() + " "
                    + "already lives in this house.");
        }
        //check if house has spare capacity
        if (!housingDatabaseInterface.houseHasSpareCapacity(assignmentRequest.getHouseToAssign())) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("House with address: "
                    + assignmentRequest.getHouseToAssign().getHouseNumber() + " does not have spare capacity.");
        }
        //identify the old House of the Occupant and map it onto House Request
        HouseInternalEntity oldHouseOfTheOccupant = housingDatabaseInterface.houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign());
        HouseRequest oldHouseOfTheOccupantMappedToHouseRequest = new HouseRequest(oldHouseOfTheOccupant.getHouseNumber());
        //reduce the capacity of the previous house of Occupant by one PROBLEM
        housingDatabaseInterface.decreaseHouseCurrentCapacityByOne(oldHouseOfTheOccupantMappedToHouseRequest);
        //assign the occupant from request to the house from request
        housingDatabaseInterface.assignSpecificOccupantToSpecificHouse(assignmentRequest.getHouseToAssign(), assignmentRequest.getOccupantToAssign());
        //increase the capacity of the house from request by one
        housingDatabaseInterface.increaseHouseCurrentCapacityByOne(assignmentRequest.getHouseToAssign());
        return ResponseEntity.ok().headers(responseHeaders).body("Occupant: "
                + assignmentRequest.getOccupantToAssign().getFirstName() + " "
                + assignmentRequest.getOccupantToAssign().getLastName() + " "
                + "who previously lived in House: " + oldHouseOfTheOccupant.toString() + " "
                + "was moved to a new house: " + assignmentRequest.getHouseToAssign().getHouseNumber());
    }
}


