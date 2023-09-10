package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.AssignmentRequest;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.services.AssignmentService;
import com.example.housingmanagement.api.services.HouseService;
import com.example.housingmanagement.api.services.OccupantService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final HouseService houseService;
    private final OccupantService occupantService;

    public AssignmentController(AssignmentService assignmentService, HouseService houseService, OccupantService occupantService) {
        this.assignmentService = assignmentService;
        this.houseService = houseService;
        this.occupantService = occupantService;
    }

    @GetMapping(value = "occupants_of_a_specific_house")
    public ResponseEntity<String> getAllOccupantsOfSpecificHouse(@RequestBody HouseRequest houseRequest) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //checks if this house exists
        if (!houseService.existsByHouse(houseRequest)) {
            return ResponseEntity.badRequest().headers(responseHeaders).body(
                    "There is no house with number " + houseRequest.getHouseNumber() + " in the Database.");
        }
        //identifies house in internal database that matches the house from request
        Optional<HouseInternalEntity> houseInternalEntity =
                Optional.ofNullable(houseService.identifyHouseInternalEntity(houseRequest));
        //returns a list of occupants of this house
        List<OccupantInternalEntity> occupantsAssignedToThisHouse =
                assignmentService.getOccupantsAssignedToThisHouseIntEnt(houseInternalEntity);

        return ResponseEntity.ok().headers(responseHeaders).body(occupantsAssignedToThisHouse.toString());

    }

    @PutMapping(value = "/occupants/assign_specific_homeless_occupant_to_specific_house")
    public ResponseEntity<String> assignSpecificHomelessOccupantToSpecificHouse(@RequestBody AssignmentRequest assignmentRequest) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //check if target house exists
        if (!houseService.existsByHouse(assignmentRequest.getHouseToAssign())) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("There is no house with address: "
                    + assignmentRequest.getHouseToAssign().getHouseNumber() + " in the database.");
        }
        //check if target occupant exists
        if (!occupantService.existsByOccupant(assignmentRequest.getOccupantToAssign())) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("There is no Occupant named: "
                    + assignmentRequest.getOccupantToAssign().getFirstName() + " "
                    + assignmentRequest.getOccupantToAssign().getLastName()
                    + " in the database.");
        }
        //check if house has spare capacity
        if (houseService.houseHasSpareCapacity(assignmentRequest.getHouseToAssign())) {
            //check if the occupant already had a house before
            if (assignmentService.
                    houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign()) != null) {
                return ResponseEntity.badRequest().headers(responseHeaders).body("Occupant: "
                        + assignmentRequest.getOccupantToAssign().getFirstName() + " "
                        + assignmentRequest.getOccupantToAssign().getLastName() + " "
                        + "already has a House: " + assignmentService
                        .houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign()).toString());
            }
            //TODO: check against gender mixing
            //assign the occupant from request to the house from request
            assignmentService.assignSpecificOccupantToSpecificHouse(assignmentRequest.getHouseToAssign(), assignmentRequest.getOccupantToAssign());
            //increase the capacity of the house from request by one
            houseService.increaseHouseCurrentCapacityByOne(assignmentRequest.getHouseToAssign());

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
        if (!houseService.existsByHouse(assignmentRequest.getHouseToAssign())) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("There is no house with address: "
                    + assignmentRequest.getHouseToAssign().getHouseNumber() + " in the database.");
        }
        //check if target occupant exists
        if (!occupantService.existsByOccupant(assignmentRequest.getOccupantToAssign())) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("There is no Occupant named: "
                    + assignmentRequest.getOccupantToAssign().getFirstName() + " "
                    + assignmentRequest.getOccupantToAssign().getLastName()
                    + " in the database.");
        }
        //check if target occupant has a house
        if (assignmentService.houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign()) == null) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("Occupant: "
                    + assignmentRequest.getOccupantToAssign().getFirstName() + " "
                    + assignmentRequest.getOccupantToAssign().getLastName() + " "
                    + " does not have a House assigned yet. Please issue a PUT request to /occupants/assign_specific_homeless_occupant_to_specific_house).");
        }
        //check if target house is the same as old house
        if (assignmentService.houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign()).toString()
                .equals(assignmentRequest.getHouseToAssign().toString())) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("Occupant: "
                    + assignmentRequest.getOccupantToAssign().getFirstName() + " "
                    + assignmentRequest.getOccupantToAssign().getLastName() + " "
                    + "already lives in this house.");
        }
        //check if house has spare capacity
        if (!houseService.houseHasSpareCapacity(assignmentRequest.getHouseToAssign())) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("House with address: "
                    + assignmentRequest.getHouseToAssign().getHouseNumber() + " does not have spare capacity.");
        }
        //identify the old House of the Occupant and map it onto House Request
        HouseInternalEntity oldHouseOfTheOccupant = assignmentService.houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign());
        HouseRequest oldHouseOfTheOccupantMappedToHouseRequest = new HouseRequest(oldHouseOfTheOccupant.getHouseNumber());
        //reduce the capacity of the previous house of Occupant by one
        houseService.decreaseHouseCurrentCapacityByOne(oldHouseOfTheOccupantMappedToHouseRequest);
        //assign the occupant from request to the house from request
        assignmentService.assignSpecificOccupantToSpecificHouse(assignmentRequest.getHouseToAssign(), assignmentRequest.getOccupantToAssign());
        //increase the capacity of the house from request by one
        houseService.increaseHouseCurrentCapacityByOne(assignmentRequest.getHouseToAssign());
        return ResponseEntity.ok().headers(responseHeaders).body("Occupant: "
                + assignmentRequest.getOccupantToAssign().getFirstName() + " "
                + assignmentRequest.getOccupantToAssign().getLastName() + " "
                + "who previously lived in House: " + oldHouseOfTheOccupant.toString() + " "
                + "was moved to a new house: " + assignmentRequest.getHouseToAssign().getHouseNumber());
    }

    @DeleteMapping(value = "/occupants")
    public ResponseEntity<String> deleteSpecificOccupant(@RequestBody OccupantRequest occupantToBeDeleted) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //check if such occupant exists at all.
        if (!occupantService.existsByOccupant(occupantToBeDeleted)) {
            return ResponseEntity.badRequest().headers(responseHeaders).body(
                    "There is no Occupant: " + occupantToBeDeleted.getFirstName() + " "
                            + occupantToBeDeleted.getLastName() + " in the Database.");
        }
        //check if occupant has a house
        if (assignmentService.houseCurrentlyAssignedToThisOccupant(occupantToBeDeleted) != null) {
            //if so, identify where the Occupant lives and reduce current capacity by 1
            HouseInternalEntity houseWhereTheOccupantLives =
                    assignmentService.houseCurrentlyAssignedToThisOccupant(occupantToBeDeleted);
            HouseRequest temporaryHouseRequest = new HouseRequest(houseWhereTheOccupantLives.getHouseNumber());
            houseService.decreaseHouseCurrentCapacityByOne(temporaryHouseRequest);
        }
        occupantService.deleteOccupantFromDatabase(occupantToBeDeleted);

        return ResponseEntity.ok().headers(responseHeaders).body("Occupant: "
                + occupantToBeDeleted.getFirstName() + " "
                + occupantToBeDeleted.getLastName() + " was deleted from Database.");
    }
}


