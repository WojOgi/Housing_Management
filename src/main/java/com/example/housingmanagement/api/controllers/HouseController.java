package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.mappers.HouseMapperInterface;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.responses.HouseResponse;
import com.example.housingmanagement.api.services.HouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/houses")
public class HouseController {
    private final HouseService houseService;
    private final HouseMapperInterface houseMapper;

    public HouseController(HouseService houseService, HouseMapperInterface houseMapper) {
        this.houseService = houseService;
        this.houseMapper = houseMapper;
    }

    @GetMapping
    public ResponseEntity<List<HouseResponse>> getAllHouses() {
        List<HouseResponse> houseResponses = houseMapper.toHouseResponseList(houseService.fetchAll());
        return ResponseEntity.ok().body(houseResponses);
    }

    @GetMapping(value = "/available")
    public ResponseEntity<List<HouseResponse>> getAvailableHouses(){
        List<HouseInternalEntity> listOfAvailableHouses = getListOfAvailableHouses();
        List<HouseResponse> houseResponses = houseMapper.toHouseResponseList(listOfAvailableHouses);
        return ResponseEntity.ok().body(houseResponses);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<HouseResponse> getHouse(@PathVariable Integer id){
        Optional<HouseInternalEntity> houseInternalEntityOptional = houseService.findById(id);
        if(houseInternalEntityOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        HouseResponse houseResponse = houseMapper.toHouseResponse(houseInternalEntityOptional.get());
        return ResponseEntity.ok().body(houseResponse);
    }

    @PostMapping
    public ResponseEntity<Void> addNewHouse(@RequestBody HouseRequest houseToBeAdded) {
        //checks if this house exists
        if (houseService.existsByHouse(houseToBeAdded)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        houseService.addHouseToDatabase(houseMapper.toHouseInternalEntity(houseToBeAdded));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
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

    private List<HouseInternalEntity> getListOfAvailableHouses() {
        return houseService.fetchAll().stream()
                .filter(houseInternalEntity -> houseInternalEntity.getCurrentCapacity() < houseInternalEntity.getMaxCapacity())
                .toList();
    }


}
