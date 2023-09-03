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
    private HouseRepositoryJPA houseRepository;
    @Autowired
    private OccupantRepositoryJPA occupantRepository;

    @Override
    public List<HouseInternalEntity> getAllHouses() {
        return houseRepository.findAll();
    }

    @Override
    public List<OccupantInternalEntity> getAllOccupants() {
        return occupantRepository.findAll();
    }

    @Override
    public void addHouseToDatabase(HouseInternalEntity houseInternalEntity) {
        houseRepository.save(houseInternalEntity);
    }

    @Override
    public void deleteHouseFromDatabase(HouseRequest houseRequest) {
        //tutaj próbowałem zrobić delete w bardziej elegancki sposób korzystając z JPA ale się nie udawało
        houseRepository.deleteById(houseRepository.findByHouseNumber(houseRequest.getHouseNumber()).getId());
    }

    @Override
    public HouseInternalEntity identifyHouseInternalEntity(HouseRequest houseRequest) {
        return houseRepository.findByHouseNumber(houseRequest.getHouseNumber());
    }

    @Override
    public List<OccupantInternalEntity> getOccupantsAssignedToThisHouseIntEnt(Optional<HouseInternalEntity> houseInternalEntity) {

        List<OccupantInternalEntity> allOccupants = occupantRepository.findAll();

        List<OccupantInternalEntity> occupantsWithAnyHouseAssigned =
                allOccupants.stream().filter(x -> x.getHouseInternalEntity() != null).toList();

        return occupantsWithAnyHouseAssigned.stream().filter(x -> x.getHouseInternalEntity().getHouseNumber()
                .equals(houseInternalEntity.get().getHouseNumber())).toList();
    }

    @Override
    public int houseCurrentCapacity(HouseRequest houseRequest) {
        return houseRepository.findByHouseNumber(houseRequest.getHouseNumber()).getCurrentCapacity();
    }

    @Override
    public boolean existsByHouse(HouseRequest houseRequest) {
        return houseRepository.existsByHouseNumber(houseRequest.getHouseNumber());
    }

    @Override
    public boolean existsByOccupant(OccupantRequest occupantRequest) {

        return occupantRepository.existsByFirstNameAndLastName(
                occupantRequest.getFirstName(), occupantRequest.getLastName());
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
        //tutaj próbowałem zrobić delete w bardziej elegancki sposób korzystając z JPA ale się nie udawało
        occupantRepository.deleteById(occupantRepository.findByFirstNameAndLastName(
                occupantRequest.getFirstName(), occupantRequest.getLastName()).getId());
    }

    @Override
    public boolean houseHasSpareCapacity(HouseRequest houseRequest) {
        Optional<HouseInternalEntity> houseInternalEntityToBeChecked = Optional.ofNullable(houseRepository.findByHouseNumber(houseRequest.getHouseNumber()));

        return houseInternalEntityToBeChecked.get().getCurrentCapacity() < houseInternalEntityToBeChecked.get().getMaxCapacity();
    }

    @Override
    public HouseInternalEntity houseCurrentlyAssignedToThisOccupant(OccupantRequest occupantRequest) {
        //identify the Occupant
        Optional<OccupantInternalEntity> occupantToCheckIfHasHouseAssigned =
                Optional.ofNullable(occupantRepository.findByFirstNameAndLastName(occupantRequest.getFirstName(),
                        occupantRequest.getLastName()));

        if (occupantToCheckIfHasHouseAssigned.get().getHouseInternalEntity() == null) {
            return null;
        }
        return occupantToCheckIfHasHouseAssigned.get().getHouseInternalEntity();
    }

    @Override
    public void increaseHouseCurrentCapacityByOne(HouseRequest houseRequest) {

        Optional<HouseInternalEntity> houseInternalEntityToIncreaseCapacityByOne =
                Optional.ofNullable(houseRepository.findByHouseNumber(houseRequest.getHouseNumber()));

        houseInternalEntityToIncreaseCapacityByOne.get().setCurrentCapacity(houseInternalEntityToIncreaseCapacityByOne.get().getCurrentCapacity() + 1);

        HouseInternalEntity houseInternalEntity = new HouseInternalEntity(
                houseInternalEntityToIncreaseCapacityByOne.get().getId(),
                houseInternalEntityToIncreaseCapacityByOne.get().getHouseNumber(),
                houseInternalEntityToIncreaseCapacityByOne.get().getMaxCapacity(),
                houseInternalEntityToIncreaseCapacityByOne.get().getCurrentCapacity());

        houseRepository.save(houseInternalEntity);
    }

    @Override
    public void decreaseHouseCurrentCapacityByOne(HouseRequest houseRequest) {

        Optional<HouseInternalEntity> houseInternalEntityToDecreaseCapacityByOne =
                Optional.ofNullable(houseRepository.findByHouseNumber(houseRequest.getHouseNumber()));

        houseInternalEntityToDecreaseCapacityByOne.get().setCurrentCapacity(houseInternalEntityToDecreaseCapacityByOne.get().getCurrentCapacity() - 1);

        HouseInternalEntity houseInternalEntity = new HouseInternalEntity(
                houseInternalEntityToDecreaseCapacityByOne.get().getId(),
                houseInternalEntityToDecreaseCapacityByOne.get().getHouseNumber(),
                houseInternalEntityToDecreaseCapacityByOne.get().getMaxCapacity(),
                houseInternalEntityToDecreaseCapacityByOne.get().getCurrentCapacity());

        houseRepository.save(houseInternalEntity);
    }

    @Override
    public void assignSpecificOccupantToSpecificHouse(HouseRequest houseRequest, OccupantRequest occupantRequest) {
        //identify House
        Optional<HouseInternalEntity> houseInternalEntityToAssign =
                Optional.ofNullable(houseRepository.findByHouseNumber(houseRequest.getHouseNumber()));
        //identify Occupant
        Optional<OccupantInternalEntity> occupantInternalEntityToAssign =
                Optional.ofNullable(occupantRepository.findByFirstNameAndLastName(occupantRequest.getFirstName(),
                        occupantRequest.getLastName()));
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



