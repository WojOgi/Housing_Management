package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.mappers.OccupantMapperInterface;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.responses.OccupantResponse;
import com.example.housingmanagement.api.services.OccupantService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OccupantController {
    private final OccupantService occupantService;
private final OccupantMapperInterface occupantMapper;

    public OccupantController(OccupantService occupantService, OccupantMapperInterface occupantMapper) {
        this.occupantService = occupantService;
        this.occupantMapper = occupantMapper;
    }


    @GetMapping(value = "/occupants")
    public ResponseEntity<List<OccupantResponse>> getAllOccupants() {
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set("get", "get all Occupants from the Database");

        List<OccupantInternalEntity> occupantInternalEntityList = occupantService.fetchAll();
        List<OccupantResponse> occupantResponseList = occupantMapper.toOccupantResponse(occupantInternalEntityList);

        return ResponseEntity.ok().headers(responseHeader).body(occupantResponseList);
    }

    @PostMapping(value = "/occupants/add_occupant_without_house")
    public ResponseEntity<String> addOccupantWithoutHouse(@RequestBody OccupantRequest occupantToBeAddedWithoutHouse) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //check if such Occupant already exists
        if (occupantService.existsByOccupant(occupantToBeAddedWithoutHouse)) {
            return ResponseEntity.badRequest().headers(responseHeaders).body(
                    "An Occupant with full name: " + occupantToBeAddedWithoutHouse.getFirstName()
                            + " " + occupantToBeAddedWithoutHouse.getLastName() + " already exists in the Database.");
        }
        //check if Gender is specified correctly
        if (!(occupantService.retrieveOccupantGenderFromRequest(occupantToBeAddedWithoutHouse).equals("M")
                ||
                occupantService.retrieveOccupantGenderFromRequest(occupantToBeAddedWithoutHouse).equals("F"))) {
            return ResponseEntity.badRequest().headers(responseHeaders).body("You must specify Occupant gender as: " +
                    "M (male) or F (female)");
        }
        //add Occupant to Database
        occupantService.addOccupantToDatabase(occupantMapper.toOccupantInternalEntity(occupantToBeAddedWithoutHouse));
        responseHeaders.set("post", "adding a new Occupant without a house to the Database");

        return ResponseEntity.ok().headers(responseHeaders).body("Added Occupant without a house: "
                + occupantToBeAddedWithoutHouse.getFirstName()
                + " " + occupantToBeAddedWithoutHouse.getLastName());
    }




}
