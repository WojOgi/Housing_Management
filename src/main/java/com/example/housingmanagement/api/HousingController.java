package com.example.housingmanagement.api;


import org.aspectj.lang.annotation.DeclareError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class HousingController {

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

    @PostMapping(value = "/housing")
    public ResponseEntity<String> addNewHouse(@RequestBody HouseRequest houseToBeAdded) {
        HttpHeaders responseHeaders = new HttpHeaders();

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
        housingDatabaseInterface.deleteHouseFromDatabase(houseToBeDeleted);

        return ResponseEntity.ok().headers(responseHeaders).body("House with address: "
                + houseToBeDeleted.getHouseNumber() + " was deleted from Database.");
    }

    @GetMapping(value = "/occupants")
    public ResponseEntity<List<OccupantResponse>> getAllOccupants() {
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set("get", "get all Occupants from the Database");

        List<OccupantInternalEntity> occupantInternalEntityList = housingDatabaseInterface.getAllOccupants();
        List<OccupantResponse> occupantResponseList = toOccupantResponse(occupantInternalEntityList);

        return ResponseEntity.ok().headers(responseHeader).body(occupantResponseList);
    }
    @GetMapping(value = "occupants_of_a_specific_house")
    public ResponseEntity<String> getAllOccupantsOfSpecificHouse(@RequestBody HouseRequest houseRequest){
        HttpHeaders responseHeaders = new HttpHeaders();
        //checks if this house exists
        if(!housingDatabaseInterface.existsByHouse(houseRequest)){
            return ResponseEntity.badRequest().headers(responseHeaders).body(
                    "There is no house with number " + houseRequest.getHouseNumber() + " in the Database.");
        }
        //identifies house in internal database that matches the house from request
        Optional<HouseInternalEntity> houseInternalEntity =
                housingDatabaseInterface.
                        identifiedHouseInDatabase(housingDatabaseInterface.identifyHouseInDatabaseByAddressFromRequest(houseRequest));
        //returns a list of occupants of this house
        List<OccupantInternalEntity> occupantsAssignedToThisHouse =
                housingDatabaseInterface.getOccupantsAssignedToThisHouseIntEnt(houseInternalEntity);

        return ResponseEntity.ok().headers(responseHeaders).body(occupantsAssignedToThisHouse.toString());

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
        if(!(   housingDatabaseInterface.retrieveOccupantGenderFromRequest(occupantToBeAddedWithoutHouse).equals("M")
                             ||
                housingDatabaseInterface.retrieveOccupantGenderFromRequest(occupantToBeAddedWithoutHouse).equals("F"))){
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

    private HouseInternalEntity toHouseInternalEntity(HouseRequest houseToBeAdded) {
        return new HouseInternalEntity(houseToBeAdded.getHouseNumber(),
                houseToBeAdded.getMaxCapacity(), 0);
    }

    private OccupantInternalEntity toOccupantInternalEntity(OccupantRequest occupantToBeAddedWithoutHouse) {
        return new OccupantInternalEntity(occupantToBeAddedWithoutHouse.getFirstName(), occupantToBeAddedWithoutHouse.getLastName()
        , occupantToBeAddedWithoutHouse.getGender());
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

    private List<OccupantResponse> toOccupantResponse(List<OccupantInternalEntity> occupantInternalEntityList) {
        List<OccupantResponse> occupantResponseList = new ArrayList<>();
        for (int i = 0; i < occupantInternalEntityList.size(); i++) {
            OccupantInternalEntity currentElement = occupantInternalEntityList.get(i);
            OccupantResponse occupantResponse = new OccupantResponse(currentElement.getFirstName(), currentElement.getLastName());
            occupantResponseList.add(occupantResponse);
        }
        return occupantResponseList;
    }

}


