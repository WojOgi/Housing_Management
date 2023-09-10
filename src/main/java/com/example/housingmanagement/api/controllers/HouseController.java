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

    @GetMapping(value = "/housing")
    public ResponseEntity<List<HouseResponse>> getAllHouses() {
        List<HouseResponse> houseResponses = houseMapper.toHouseResponse(houseService.fetchAll());
        return ResponseEntity.ok().body(houseResponses);
    }

    @PostMapping(value = "/housing")
    public ResponseEntity<Void> addNewHouse(@RequestBody HouseRequest houseToBeAdded) {
        //checks if this house exists
        if (houseService.existsByHouse(houseToBeAdded)) {
            System.out.println("A House with number " + houseToBeAdded.getHouseNumber() + " already exists");
            return ResponseEntity.badRequest().build();
        }
        houseService.addHouseToDatabase(houseMapper.toHouseInternalEntity(houseToBeAdded));
        System.out.println("Added House with address: " + houseToBeAdded.getHouseNumber());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(value = "/housing")
    public ResponseEntity<Void> deleteSpecificHouse(@RequestBody HouseRequest houseToBeDeleted) {
        //check if such house exists in Database
        if (!houseService.existsByHouse(houseToBeDeleted)) {
            System.out.println("There is no house with number " + houseToBeDeleted.getHouseNumber() + " in the Database.");
            return ResponseEntity.badRequest().build();
        }
        //check if the House is occupied by anybody
        if (houseService.houseCurrentCapacity(houseToBeDeleted) > 0) {
            System.out.println("This house is occupied. House needs to be vacated before removal from the Database.");
            return ResponseEntity.badRequest().build();
        }
        houseService.deleteHouseFromDatabase(houseToBeDeleted);
        System.out.println("House with address: " + houseToBeDeleted.getHouseNumber() + " was deleted from Database.");

        return ResponseEntity.ok().build();
    }


}
