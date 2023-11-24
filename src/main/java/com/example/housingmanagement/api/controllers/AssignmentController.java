package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.dbentities.Gender;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.mappers.HouseMapperInterface;
import com.example.housingmanagement.api.mappers.OccupantMapperInterface;
import com.example.housingmanagement.api.requests.AssignmentRequest;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.responses.OccupantResponse;
import com.example.housingmanagement.api.services.AssignmentService;
import com.example.housingmanagement.api.services.HouseService;
import com.example.housingmanagement.api.services.OccupantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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

    @GetMapping(value = "/occupants_of_house")
    public ResponseEntity<List<OccupantResponse>> getAllOccupantsOfSpecificHouse(@RequestBody HouseRequest houseRequest) {
        if (isInvalidHouseRequest(houseRequest)) {
            return ResponseEntity.badRequest().build();
        }
        //checks if this house exists
        if (!houseService.existsByHouse(houseRequest)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        //identifies house in internal database that matches the house from request and
        //returns a list of occupants of this house
        List<OccupantResponse> occupantResponseList =
                occupantMapper.toOccupantResponseList(
                        assignmentService.getOccupantsAssignedToThisHouseIntEnt(
                                houseMapper.toHouseInternalEntity(houseRequest)));
        return ResponseEntity.ok().body(occupantResponseList);
    }

    @PutMapping(value = "/occupants/assign")
    public ResponseEntity<Void> assignSpecificHomelessOccupantToSpecificHouse(@RequestBody AssignmentRequest assignmentRequest) {
        if (isInvalidAssignmentRequest(assignmentRequest)) {
            return ResponseEntity.badRequest().build();
        }
        //check if target house exists and target occupant exists
        if (houseOrOccupantDontExist(assignmentRequest)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        //check if house has spare capacity
        if (houseHasSpareCapacity(assignmentRequest)) {
            //check if the occupant already had a house before
            if (occupantHasAHouse(assignmentRequest)) {
                return ResponseEntity.unprocessableEntity().build();
            }

            List<Gender> genderList = getGenderList(assignmentRequest);
            OccupantInternalEntity occupantInternalEntity = getOccupantInternalEntity(assignmentRequest);

            if (noGenderConflict(genderList, occupantInternalEntity)) {
                //assign the occupant from request to the house from request
                assignmentService.assignSpecificOccupantToSpecificHouse(assignmentRequest.getHouseToAssign(), assignmentRequest.getOccupantToAssign());
                //increase the capacity of the house from request by one
                houseService.increaseHouseCurrentCapacityByOne(assignmentRequest.getHouseToAssign());
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    @PutMapping(value = "/occupants/move")
    public ResponseEntity<Void> moveSpecificOccupantToDifferentHouse(@RequestBody AssignmentRequest assignmentRequest) {
        if (isInvalidAssignmentRequest(assignmentRequest)) {
            return ResponseEntity.badRequest().build();
        }
        //check if target house exists and target occupant exists
        //check if target occupant has a house that is different from current house and has spare capacity
        if (houseOrOccupantDontExist(assignmentRequest) || houseSpecifiedIncorrectly(assignmentRequest)) {
            return ResponseEntity.unprocessableEntity().build();
        }





        if (houseHasSpareCapacity(assignmentRequest)) {
            //check if occupant was homeless - in such case should use /assign endpoint
            if (!occupantHasAHouse(assignmentRequest)) {
                return ResponseEntity.unprocessableEntity().build();
            }

            List<Gender> genderList = getGenderList(assignmentRequest);
            OccupantInternalEntity occupantInternalEntity = getOccupantInternalEntity(assignmentRequest);

            if (noGenderConflict(genderList, occupantInternalEntity)) {

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
        }




        return ResponseEntity.unprocessableEntity().build();
    }

    @DeleteMapping(value = "/occupants")
    public ResponseEntity<Void> deleteSpecificOccupant(@RequestBody OccupantRequest occupantToBeDeleted) {
        if (isInvalidOccupantRequest(occupantToBeDeleted)) {
            return ResponseEntity.badRequest().build();
        }
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

    private boolean occupantHasAHouse(AssignmentRequest assignmentRequest) {
        return assignmentService.
                houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign()) != null;
    }

    private boolean houseHasSpareCapacity(AssignmentRequest assignmentRequest) {
        return houseService.houseHasSpareCapacity(assignmentRequest.getHouseToAssign());
    }

    private static boolean noGenderConflict(List<Gender> genderList, OccupantInternalEntity occupantInternalEntity) {
        return genderList.isEmpty() || genderList.contains(occupantInternalEntity.getGender());
    }

    private OccupantInternalEntity getOccupantInternalEntity(AssignmentRequest assignmentRequest) {
        return occupantService.findByFirstAndLastName(assignmentRequest.getOccupantToAssign()
                .getFirstName(), assignmentRequest.getOccupantToAssign().getLastName());
    }

    private List<Gender> getGenderList(AssignmentRequest assignmentRequest) {
        HouseInternalEntity houseInternalEntity = getHouseInternalEntity(assignmentRequest);
        List<OccupantInternalEntity> occupantInternalEntityList = assignmentService.getOccupantsAssignedToThisHouseIntEnt(houseInternalEntity);
        return occupantInternalEntityList.stream()
                .map(OccupantInternalEntity::getGender)
                .toList();
    }

    private HouseInternalEntity getHouseInternalEntity(AssignmentRequest assignmentRequest) {
        return houseMapper.toHouseInternalEntity(assignmentRequest.getHouseToAssign());
    }

    private boolean houseOrOccupantDontExist(AssignmentRequest assignmentRequest) {
        return !houseService.existsByHouse(assignmentRequest.getHouseToAssign())
                || !occupantService.existsByOccupant(assignmentRequest.getOccupantToAssign());

    }

    private boolean houseSpecifiedIncorrectly(AssignmentRequest assignmentRequest) {
        return assignmentService.houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign()) == null
                || assignmentService.houseCurrentlyAssignedToThisOccupant(assignmentRequest.getOccupantToAssign()).toString()
                .equals(assignmentRequest.getHouseToAssign().toString())
                || !houseHasSpareCapacity(assignmentRequest);
    }

    private static boolean isInvalidAssignmentRequest(AssignmentRequest assignmentRequest) {
        return Objects.isNull(assignmentRequest)
                || StringUtils.isBlank(assignmentRequest.getOccupantToAssign().getFirstName())
                || StringUtils.isBlank(assignmentRequest.getOccupantToAssign().getLastName())
                || Objects.isNull(assignmentRequest.getOccupantToAssign().getGender())
                || StringUtils.isBlank(assignmentRequest.getHouseToAssign().getHouseNumber());
    }

    private static boolean isInvalidHouseRequest(HouseRequest houseRequest) {
        return Objects.isNull(houseRequest) || StringUtils.isBlank(houseRequest.getHouseNumber());
    }

    private static boolean isInvalidOccupantRequest(OccupantRequest occupantRequest) {
        return Objects.isNull(occupantRequest)
                || StringUtils.isBlank(occupantRequest.getFirstName())
                || StringUtils.isBlank(occupantRequest.getLastName());
    }

}


