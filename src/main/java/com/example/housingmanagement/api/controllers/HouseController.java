package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.mappers.HouseMapperInterface;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.responses.HouseResponse;
import com.example.housingmanagement.api.services.HouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HouseController {
    private final HouseService houseService;
    private final HouseMapperInterface houseMapper;

    public HouseController(HouseService houseService, HouseMapperInterface houseMapper) {
        this.houseService = houseService;
        this.houseMapper = houseMapper;
    }

    @GetMapping(value = "/houses")
    public ResponseEntity<List<HouseResponse>> getAllHouses() {
        List<HouseResponse> houseResponses = houseMapper.toHouseResponse(houseService.fetchAll());
        return ResponseEntity.ok().body(houseResponses);
    }

    @PostMapping(value = "/houses")
    public ResponseEntity<Void> addNewHouse(@RequestBody HouseRequest houseToBeAdded) {
        //checks if this house exists
        if (houseService.existsByHouse(houseToBeAdded)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        houseService.addHouseToDatabase(houseMapper.toHouseInternalEntity(houseToBeAdded));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(value = "/houses")
    public ResponseEntity<Void> deleteSpecificHouse(@RequestBody HouseRequest houseToBeDeleted) {
        //check if such house exists in Database
        if (!houseService.existsByHouse(houseToBeDeleted)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        //check if the House is occupied by anybody
        if (houseService.houseCurrentCapacity(houseToBeDeleted) > 0) {
            return ResponseEntity.unprocessableEntity().build();
        }
        houseService.deleteHouseFromDatabase(houseToBeDeleted);
        return ResponseEntity.ok().build();
    }


}
