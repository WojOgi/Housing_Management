package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.responses.HouseResponse;
import com.example.housingmanagement.api.services.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HouseController {
    @Autowired
    private HouseService houseService;

    @GetMapping(value = "/housing")
    public ResponseEntity<List<HouseResponse>> getAllHouses() {
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set("get", "get all Houses from the Database");

        List<HouseInternalEntity> allHouseInternalEntities = houseService.fetchAll();
        List<HouseResponse> houseResponses = toHouseResponse(allHouseInternalEntities);

        return ResponseEntity.ok().headers(responseHeader).body(houseResponses);
    }

    @PostMapping(value = "/housing")
    public ResponseEntity<String> addNewHouse(@RequestBody HouseRequest houseToBeAdded) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //checks if this house exists
        if (houseService.existsByHouse(houseToBeAdded)) {
            return ResponseEntity.badRequest().headers(responseHeaders).body(
                    "A House with number " + houseToBeAdded.getHouseNumber() + " already exists");
        }
        houseService.addHouseToDatabase(toHouseInternalEntity(houseToBeAdded));
        responseHeaders.set("post", "adding a new House to the Database");

        return ResponseEntity.ok().headers(responseHeaders).body("Added House with address: "
                + houseToBeAdded.getHouseNumber());
    }

    @DeleteMapping(value = "/housing")
    public ResponseEntity<String> deleteSpecificHouse(@RequestBody HouseRequest houseToBeDeleted) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //check if such house exists in Database
        if (!houseService.existsByHouse(houseToBeDeleted)) {
            return ResponseEntity.badRequest().headers(responseHeaders).body(
                    "There is no house with number " + houseToBeDeleted.getHouseNumber() + " in the Database.");
        }
        //check if the House is occupied by anybody
        if (houseService.houseCurrentCapacity(houseToBeDeleted) > 0) {
            return ResponseEntity.badRequest().headers(responseHeaders).body(
                    "This house is occupied. House needs to be vacated before removal from the Database.");
        }
        houseService.deleteHouseFromDatabase(houseToBeDeleted);

        return ResponseEntity.ok().headers(responseHeaders).body("House with address: "
                + houseToBeDeleted.getHouseNumber() + " was deleted from Database.");
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

    private HouseInternalEntity toHouseInternalEntity(HouseRequest houseToBeAdded) {
        return new HouseInternalEntity(houseToBeAdded.getHouseNumber(),
                houseToBeAdded.getMaxCapacity(), 0);
    }


}
