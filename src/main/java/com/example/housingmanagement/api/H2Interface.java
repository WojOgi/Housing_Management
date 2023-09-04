package com.example.housingmanagement.api;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.services.AssignmentService;
import com.example.housingmanagement.api.services.HouseService;
import com.example.housingmanagement.api.services.OccupantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class H2Interface implements HousingDatabaseInterface {
    @Autowired
    private HouseService houseService;
    @Autowired
    private OccupantService occupantService;
    @Autowired
    private AssignmentService assignmentService;

    @Override
    public List<HouseInternalEntity> getAllHouses() {
        return houseService.fetchAll();
    }

    @Override
    public List<OccupantInternalEntity> getAllOccupants() {
        return occupantService.fetchAll();
    }

    @Override
    public void addHouseToDatabase(HouseInternalEntity houseInternalEntity) {
        houseService.addHouseToDatabase(houseInternalEntity);
    }

    @Override
    @Transactional
    public void deleteHouseFromDatabase(HouseRequest houseRequest) {
        houseService.deleteHouseFromDatabase(houseRequest);
    }

    @Override
    public HouseInternalEntity identifyHouseInternalEntity(HouseRequest houseRequest) {
        return houseService.identifyHouseInternalEntity(houseRequest);
    }

    @Override
    public List<OccupantInternalEntity> getOccupantsAssignedToThisHouseIntEnt(Optional<HouseInternalEntity> houseInternalEntity) {

        return assignmentService.getOccupantsAssignedToThisHouseIntEnt(houseInternalEntity);
    }

    @Override
    public int houseCurrentCapacity(HouseRequest houseRequest) {
        return houseService.houseCurrentCapacity(houseRequest);
    }

    @Override
    public boolean existsByHouse(HouseRequest houseRequest) {
        return houseService.existsByHouse(houseRequest);
    }

    @Override
    public boolean existsByOccupant(OccupantRequest occupantRequest) {
        return occupantService.existsByOccupant(occupantRequest);

    }

    @Override
    public String retrieveOccupantGenderFromRequest(OccupantRequest occupantRequest) {
        return occupantRequest.getGender();
    }

    @Override
    public String retrieveOccupantGenderFromInternalEntity(OccupantInternalEntity occupantInternalEntity) {
        return occupantService.retrieveOccupantGenderFromInternalEntity(occupantInternalEntity);
    }


    @Override
    public void addOccupantToDatabase(OccupantInternalEntity occupantInternalEntity) {
        occupantService.addOccupantToDatabase(occupantInternalEntity);
    }

    @Override
    @Transactional
    public void deleteOccupantFromDatabase(OccupantRequest occupantRequest) {
        occupantService.deleteOccupantFromDatabase(occupantRequest);
    }

    @Override
    public boolean houseHasSpareCapacity(HouseRequest houseRequest) {
        return houseService.houseHasSpareCapacity(houseRequest);
    }

    @Override
    public HouseInternalEntity houseCurrentlyAssignedToThisOccupant(OccupantRequest occupantRequest) {
        return assignmentService.houseCurrentlyAssignedToThisOccupant(occupantRequest);
    }

    @Override
    public void increaseHouseCurrentCapacityByOne(HouseRequest houseRequest) {
        houseService.increaseHouseCurrentCapacityByOne(houseRequest);
    }

    @Override
    public void decreaseHouseCurrentCapacityByOne(HouseRequest houseRequest) {
        houseService.decreaseHouseCurrentCapacityByOne(houseRequest);
    }

    @Override
    public void assignSpecificOccupantToSpecificHouse(HouseRequest houseRequest, OccupantRequest occupantRequest) {
        assignmentService.assignSpecificOccupantToSpecificHouse(houseRequest, occupantRequest);
    }
}



