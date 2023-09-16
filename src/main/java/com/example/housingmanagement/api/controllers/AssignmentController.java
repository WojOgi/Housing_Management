package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.mappers.HouseMapperInterface;
import com.example.housingmanagement.api.mappers.OccupantMapperInterface;
import com.example.housingmanagement.api.requests.AssignmentRequest;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.responses.OccupantResponse;
import com.example.housingmanagement.api.services.AssignmentService;
import com.example.housingmanagement.api.services.HouseService;
import com.example.housingmanagement.api.services.OccupantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final HouseService houseService;
    private final OccupantService occupantService;
    private final HouseMapperInterface houseMapper;
    private final OccupantMapperInterface occupantMapper;


    public AssignmentController(AssignmentService assignmentService, HouseService houseService, OccupantService occupantService, HouseMapperInterface houseMapper, OccupantMapperInterface occupantMapper) {
        this.assignmentService = assignmentService;
        this.houseService = houseService;
        this.occupantService = occupantService;
        this.houseMapper = houseMapper;
        this.occupantMapper = occupantMapper;
    }

    @GetMapping(value = "/occupants_of_a_specific_house")
    public ResponseEntity<List<OccupantResponse>> getAllOccupantsOfSpecificHouse(@RequestBody HouseRequest houseRequest) {
        //checks if this house exists
        if (!houseService.existsByHouse(houseRequest)) {
            return ResponseEntity.badRequest().build();
        }
        //identifies house in internal database that matches the house from request and
        //returns a list of occupants of this house
        List<OccupantResponse> occupantResponseList =
                occupantMapper.toOccupantResponse(
                        assignmentService.getOccupantsAssignedToThisHouseIntEnt(
                                Optional.ofNullable(houseMapper.toHouseInternalEntity(houseRequest))));
        return ResponseEntity.ok().body(occupantResponseList);
    }

    @PutMapping(value = "/occupants/assign_specific_homeless_occupant_to_specific_house")
    public ResponseEntity<Void> assignSpecificHomelessOccupantToSpecificHouse(@RequestBody AssignmentRequest assignmentRequest) {
        //check if target house exists
        if (!houseService.existsByHouse(assignmentRequest.getHouseToAssign())) {
            return ResponseEntity.badRequest().build();
        }
        //check if target occupant exists
        if (!occupantService.existsByOccupant(assignmentRequest.getOccupantToAssign())) {
            return ResponseEntity.badRequest().build();
        }
        //check if house has spare capacity
        if (houseService.houseHasSpareCapacity(assignmentRequest.getHouseToAssign())) {
            //check if the occupant already had a house before
            if (assignmentService.
                    houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign()) != null) {
                return ResponseEntity.badRequest().build();
            }
            //TODO: check against gender mixing
            //assign the occupant from request to the house from request
            assignmentService.assignSpecificOccupantToSpecificHouse(assignmentRequest.getHouseToAssign(), assignmentRequest.getOccupantToAssign());
            //increase the capacity of the house from request by one
            houseService.increaseHouseCurrentCapacityByOne(assignmentRequest.getHouseToAssign());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping(value = "/occupants/move_occupant_to_different_house")
    public ResponseEntity<Void> moveSpecificOccupantToDifferentHouse(@RequestBody AssignmentRequest assignmentRequest) {
        //check if target house exists
        if (!houseService.existsByHouse(assignmentRequest.getHouseToAssign())) {
            return ResponseEntity.badRequest().build();
        }
        //check if target occupant exists
        if (!occupantService.existsByOccupant(assignmentRequest.getOccupantToAssign())) {
            return ResponseEntity.badRequest().build();
        }
        //check if target occupant has a house
        if (assignmentService.houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign()) == null) {
            return ResponseEntity.badRequest().build();
        }
        //check if target house is the same as old house
        if (assignmentService.houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign()).toString()
                .equals(assignmentRequest.getHouseToAssign().toString())) {
            return ResponseEntity.badRequest().build();
        }
        //check if house has spare capacity
        if (!houseService.houseHasSpareCapacity(assignmentRequest.getHouseToAssign())) {
            return ResponseEntity.badRequest().build();
        }
        //TODO: check against gender mixing
        //identify the old House of the Occupant and map it onto House Request
        HouseRequest oldHouseOfTheOccupantMappedToHouseRequest =
                new HouseRequest(assignmentService.houseCurrentlyAssignedToThisOccupant(
                        assignmentRequest.getOccupantToAssign()).getHouseNumber());
        //reduce the capacity of the previous house of Occupant by one
        houseService.decreaseHouseCurrentCapacityByOne(oldHouseOfTheOccupantMappedToHouseRequest);
        //assign the occupant from request to the house from request
        assignmentService.assignSpecificOccupantToSpecificHouse(assignmentRequest.getHouseToAssign(), assignmentRequest.getOccupantToAssign());
        //increase the capacity of the house from request by one
        houseService.increaseHouseCurrentCapacityByOne(assignmentRequest.getHouseToAssign());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/occupants")
    public ResponseEntity<Void> deleteSpecificOccupant(@RequestBody OccupantRequest occupantToBeDeleted) {
        //check if such occupant exists at all.
        if (!occupantService.existsByOccupant(occupantToBeDeleted)) {
            return ResponseEntity.badRequest().build();
        }
        //check if occupant has a house
        if (assignmentService.houseCurrentlyAssignedToThisOccupant(occupantToBeDeleted) != null) {
            //if so, identify where the Occupant lives and reduce current capacity by 1
            houseService.decreaseHouseCurrentCapacityByOne(
                    new HouseRequest(assignmentService.houseCurrentlyAssignedToThisOccupant(occupantToBeDeleted).getHouseNumber()));
        }
        occupantService.deleteOccupantFromDatabase(occupantToBeDeleted);
        return ResponseEntity.ok().build();
    }
}


