package com.example.housingmanagement.api;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.requests.OccupantRequest;
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
        houseRepository.deleteById(identifyHouseInDatabaseByAddressFromRequest(houseRequest));
    }

    @Override
    public int identifyHouseInDatabaseByAddressFromRequest(HouseRequest houseRequest) {
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
    public Optional<HouseInternalEntity> identifiedHouseInDatabase(int houseId) {
        return houseRepository.findById(houseId);
    }

    @Override
    public List<OccupantInternalEntity> getOccupantsAssignedToThisHouseIntEnt(Optional<HouseInternalEntity> houseInternalEntity) {

        List<OccupantInternalEntity> allOccupants = (List<OccupantInternalEntity>) occupantRepository.findAll();

        List<OccupantInternalEntity> occupantsWithAnyHouseAssigned =
                allOccupants.stream().filter(x -> x.getHouseInternalEntity() != null).toList();

        return occupantsWithAnyHouseAssigned.stream().filter(x->x.getHouseInternalEntity().getHouseNumber()
                .equals(houseInternalEntity.get().getHouseNumber())).toList();


    }

    @Override
    public int houseCurrentCapacity(HouseRequest houseRequest) {
        return houseRepository.findById(identifyHouseInDatabaseByAddressFromRequest(houseRequest)).get().getCurrentOccupancy();
    }

    @Override
    public boolean existsByHouse(HouseRequest houseRequest) {
        return identifyHouseInDatabaseByAddressFromRequest(houseRequest) > 0;
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
    public String retrieveOccupantGenderFromRequest(OccupantRequest occupantRequest) {
        return occupantRequest.getGender();
    }

    @Override
    public String retrieveOccupantGenderFromInternalEntity(OccupantInternalEntity occupantInternalEntity) {
        return occupantInternalEntity.getGender();
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
        Optional<HouseInternalEntity> houseInternalEntityToBeChecked = houseRepository.findById(identifyHouseInDatabaseByAddressFromRequest(houseRequest));

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
                houseRepository.findById(identifyHouseInDatabaseByAddressFromRequest(houseRequest));

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
                houseRepository.findById(identifyHouseInDatabaseByAddressFromRequest(houseRequest));

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
                houseRepository.findById(identifyHouseInDatabaseByAddressFromRequest(houseRequest));
        //identify Occupant
        Optional<OccupantInternalEntity> occupantInternalEntityToAssign =
                occupantRepository.findById(identifyOccupantByItsFirstAndLastName(occupantRequest));

        //update database entry for the identified occupant

        OccupantInternalEntity occupantInternalEntityToBeModified =
                new OccupantInternalEntity(
                        occupantInternalEntityToAssign.get().getId(),
                        occupantInternalEntityToAssign.get().getFirstName(),
                        occupantInternalEntityToAssign.get().getLastName(),
                        occupantInternalEntityToAssign.get().getGender(),
                        houseInternalEntityToAssign.get());
        occupantRepository.save(occupantInternalEntityToBeModified);
    }


}



