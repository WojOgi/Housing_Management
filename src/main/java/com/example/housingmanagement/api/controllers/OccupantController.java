package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.dbentities.Gender;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.mappers.OccupantMapperInterface;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.responses.OccupantResponse;
import com.example.housingmanagement.api.services.OccupantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.example.housingmanagement.api.dbentities.Gender.FEMALE;
import static com.example.housingmanagement.api.dbentities.Gender.MALE;

@RestController
@RequestMapping(value = "/occupants")
public class OccupantController {
    private final OccupantService occupantService;
    private final OccupantMapperInterface occupantMapper;

    private static final List<Gender> SUPPORTED_GENDERS = List.of(MALE, FEMALE);

    public OccupantController(OccupantService occupantService, OccupantMapperInterface occupantMapper) {
        this.occupantService = occupantService;
        this.occupantMapper = occupantMapper;
    }

    @GetMapping
    public ResponseEntity<List<OccupantResponse>> getAllOccupants() {
        List<OccupantResponse> occupantResponseList = occupantMapper.toOccupantResponseList(occupantService.fetchAll());
        return ResponseEntity.ok().body(occupantResponseList);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<OccupantResponse> getOccupant(@PathVariable Integer id) {
        Optional<OccupantInternalEntity> occupantInternalEntityOptional = occupantService.findById(id);
        if (occupantInternalEntityOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        OccupantResponse occupantResponse = occupantMapper.toOccupantResponseList(occupantInternalEntityOptional.get());
        return ResponseEntity.ok().body(occupantResponse);
    }

    @PostMapping
    public ResponseEntity<Void> addOccupantWithoutHouse(@RequestBody OccupantRequest occupantToBeAddedWithoutHouse) {
        //check if such Occupant already exists
        if (occupantService.existsByOccupant(occupantToBeAddedWithoutHouse)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        //check if Gender is specified correctly
        if (!genderSpecifiedCorrectly(occupantToBeAddedWithoutHouse)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        //add Occupant to Database
        occupantService.addOccupantToDatabase(occupantMapper.toOccupantInternalEntity(occupantToBeAddedWithoutHouse));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private boolean genderSpecifiedCorrectly(OccupantRequest occupantRequest) {
        return SUPPORTED_GENDERS.contains(occupantRequest.getGender());
    }
}

