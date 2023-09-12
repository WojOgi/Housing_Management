package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.mappers.OccupantMapperInterface;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.responses.OccupantResponse;
import com.example.housingmanagement.api.services.OccupantService;
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
        List<OccupantResponse> occupantResponseList = occupantMapper.toOccupantResponse(occupantService.fetchAll());
        return ResponseEntity.ok().body(occupantResponseList);
    }

    @PostMapping(value = "/occupants/add_occupant_without_house")
    public ResponseEntity<Void> addOccupantWithoutHouse(@RequestBody OccupantRequest occupantToBeAddedWithoutHouse) {
        //check if such Occupant already exists
        if (occupantService.existsByOccupant(occupantToBeAddedWithoutHouse)) {
            System.out.println("An Occupant with full name: " + occupantToBeAddedWithoutHouse.getFirstName()
                    + " " + occupantToBeAddedWithoutHouse.getLastName() + " already exists in the Database.");
            return ResponseEntity.badRequest().build();
        }
        //check if Gender is specified correctly
        if (!(occupantService.retrieveOccupantGenderFromRequest(occupantToBeAddedWithoutHouse).equals("M")
                ||
                occupantService.retrieveOccupantGenderFromRequest(occupantToBeAddedWithoutHouse).equals("F"))) {
            System.out.println("You must specify Occupant gender as: " + "M (male) or F (female)");
            return ResponseEntity.badRequest().build();
        }
        //add Occupant to Database
        occupantService.addOccupantToDatabase(occupantMapper.toOccupantInternalEntity(occupantToBeAddedWithoutHouse));
        System.out.println("Added Occupant without a house: " + occupantToBeAddedWithoutHouse.getFirstName()
                + " " + occupantToBeAddedWithoutHouse.getLastName());
        return ResponseEntity.ok().build();
    }
}
