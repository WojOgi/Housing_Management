package com.example.housingmanagement.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Primary

public class H2Repository implements HousingDatabaseInterface {

    @Autowired
    HouseRepository houseRepository;

    @Autowired
    OccupantRepository occupantRepository;


    @Override
    public List<HouseInternalEntity> getAllHouses() {
        return (List<HouseInternalEntity>) houseRepository.findAll();
    }

    @Override
    public List<OccupantInternalEntity> getAllOccupants() {
        return (List<OccupantInternalEntity>) occupantRepository.findAll();
    }

    @Override
    public void addHouseToDatabase(HouseInternalEntity houseInternalEntity) {
        houseRepository.save(houseInternalEntity);
    }

    @Override
    public void deleteHouseFromDatabase(HouseRequest houseRequest) {
        houseRepository.deleteById(identifyHouseInDatabaseByAddress(houseRequest));
    }

    @Override
    public int identifyHouseInDatabaseByAddress(HouseRequest houseRequest) {
        List<HouseInternalEntity> houseInternalEntityList = (List<HouseInternalEntity>) houseRepository.findAll();

        for (int i = 0; i < houseInternalEntityList.size(); i++) {

            String houseNumberFromInternalEntityList = houseInternalEntityList.get(i).getHouseNumber();
            String houseNumberFromHouseRequest = houseRequest.getHouseNumber();
            if (houseNumberFromHouseRequest.equals(houseNumberFromInternalEntityList)) {
                return houseInternalEntityList.get(i).getId();
            }
        }
        return 0;
    }

    @Override
    public int houseCurrentCapacity(HouseRequest houseRequest) {
        return houseRepository.findById(identifyHouseInDatabaseByAddress(houseRequest)).get().getCurrentOccupancy();
    }

    @Override
    public boolean existsByHouse(HouseRequest houseRequest) {
        return identifyHouseInDatabaseByAddress(houseRequest) > 0;
    }

    @Override
    public int identifyOccupantByItsFirstAndLastName(OccupantRequest occupantRequest) {
        List<OccupantInternalEntity> occupantInternalEntityList = (List<OccupantInternalEntity>) occupantRepository.findAll();
        for (int i = 0; i < occupantInternalEntityList.size(); i++) {

            String occupantFirstNameFromInternalOccupantList = occupantInternalEntityList.get(i).getFirstName();
            String occupantLastNameFromInternalOccupantList = occupantInternalEntityList.get(i).getLastName();

            String occupantFirstNameFromRequest = occupantRequest.getFirstName();
            String occupantLastNameFromRequest = occupantRequest.getLastName();

            if (occupantFirstNameFromInternalOccupantList.equals(occupantFirstNameFromRequest)
                    && occupantLastNameFromInternalOccupantList.equals(occupantLastNameFromRequest)) {
                return occupantInternalEntityList.get(i).getId();
            }
        }
        return 0;
    }

    @Override
    public boolean existsByOccupant(OccupantRequest occupantRequest) {

        return identifyOccupantByItsFirstAndLastName(occupantRequest) > 0;
    }


    @Override
    public void addOccupantToDatabase(OccupantInternalEntity occupantInternalEntity) {
        occupantRepository.save(occupantInternalEntity);
    }

    @Override
    public void deleteOccupantFromDatabase(OccupantRequest occupantRequest) {
        occupantRepository.deleteById(identifyOccupantByItsFirstAndLastName(occupantRequest));
    }

    @Override
    public boolean houseHasSpareCapacity(HouseRequest houseRequest) {
        Optional<HouseInternalEntity> houseInternalEntityToBeChecked = houseRepository.findById(identifyHouseInDatabaseByAddress(houseRequest));

        return houseInternalEntityToBeChecked.get().getCurrentOccupancy() < houseInternalEntityToBeChecked.get().getMaxCapacity();
    }

    @Override
    public HouseInternalEntity houseCurrentlyAssignedToThisOccupant(OccupantRequest occupantRequest) {
        //identify the Occupant
        Optional<OccupantInternalEntity> occupantToCheckIfHasHouseAssigned =
                occupantRepository.findById(identifyOccupantByItsFirstAndLastName(occupantRequest));

        if (occupantToCheckIfHasHouseAssigned.get().getHouseInternalEntity() == null) {
            return null;
        }
        return occupantToCheckIfHasHouseAssigned.get().getHouseInternalEntity();
    }

    @Override
    public void increaseHouseCurrentCapacityByOne(HouseRequest houseRequest) {

        Optional<HouseInternalEntity> houseInternalEntityToIncreaseCapacityByOne =
                houseRepository.findById(identifyHouseInDatabaseByAddress(houseRequest));

        houseInternalEntityToIncreaseCapacityByOne.get().setCurrentOccupancy(houseInternalEntityToIncreaseCapacityByOne.get().getCurrentOccupancy() + 1);


        HouseInternalEntity houseInternalEntity = new HouseInternalEntity(
                houseInternalEntityToIncreaseCapacityByOne.get().getId(),
                houseInternalEntityToIncreaseCapacityByOne.get().getHouseNumber(),
                houseInternalEntityToIncreaseCapacityByOne.get().getMaxCapacity(),
                houseInternalEntityToIncreaseCapacityByOne.get().getCurrentOccupancy());

        houseRepository.save(houseInternalEntity);
    }

    @Override
    public void decreaseHouseCurrentCapacityByOne(HouseRequest houseRequest) {

        Optional<HouseInternalEntity> houseInternalEntityToDecreaseCapacityByOne =
                houseRepository.findById(identifyHouseInDatabaseByAddress(houseRequest));

        houseInternalEntityToDecreaseCapacityByOne.get().setCurrentOccupancy(houseInternalEntityToDecreaseCapacityByOne.get().getCurrentOccupancy() - 1);


        HouseInternalEntity houseInternalEntity = new HouseInternalEntity(
                houseInternalEntityToDecreaseCapacityByOne.get().getId(),
                houseInternalEntityToDecreaseCapacityByOne.get().getHouseNumber(),
                houseInternalEntityToDecreaseCapacityByOne.get().getMaxCapacity(),
                houseInternalEntityToDecreaseCapacityByOne.get().getCurrentOccupancy());

        houseRepository.save(houseInternalEntity);

    }

    @Override
    public void assignSpecificOccupantToSpecificHouse(HouseRequest houseRequest, OccupantRequest occupantRequest) {

        //identify House
        Optional<HouseInternalEntity> houseInternalEntityToAssign =
                houseRepository.findById(identifyHouseInDatabaseByAddress(houseRequest));
        //identify Occupant
        Optional<OccupantInternalEntity> occupantInternalEntityToAssign =
                occupantRepository.findById(identifyOccupantByItsFirstAndLastName(occupantRequest));

        //update database entry for the identified occupant

        OccupantInternalEntity occupantInternalEntityToBeModified =
                new OccupantInternalEntity(
                        occupantInternalEntityToAssign.get().getId(),
                        occupantInternalEntityToAssign.get().getFirstName(),
                        occupantInternalEntityToAssign.get().getLastName(),
                        houseInternalEntityToAssign.get());
        occupantRepository.save(occupantInternalEntityToBeModified);
    }


}



