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

@RestController(value = "/occupants")
public class OccupantController {
    private static final List<String> SUPPORTED_GENDERS = List.of("M", "F");
    private final OccupantService occupantService;
    private final OccupantMapperInterface occupantMapper;


    public OccupantController(OccupantService occupantService, OccupantMapperInterface occupantMapper) {
        this.occupantService = occupantService;
        this.occupantMapper = occupantMapper;
    }

    @GetMapping
    public ResponseEntity<List<OccupantResponse>> getAllOccupants() {
        List<OccupantResponse> occupantResponseList = occupantMapper.toOccupantResponse(occupantService.fetchAll());
        return ResponseEntity.ok().body(occupantResponseList);
    }

    @PostMapping
    public ResponseEntity<Void> addOccupantWithoutHouse(@RequestBody OccupantRequest occupantToBeAddedWithoutHouse) {
        //check if such Occupant already exists
        if (occupantService.existsByOccupant(occupantToBeAddedWithoutHouse)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        //check if Gender is specified correctly
        if (genderSpecifiedIncorrectly(occupantToBeAddedWithoutHouse)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        //add Occupant to Database
        occupantService.addOccupantToDatabase(occupantMapper.toOccupantInternalEntity(occupantToBeAddedWithoutHouse));
        return ResponseEntity.ok().build();
    }

