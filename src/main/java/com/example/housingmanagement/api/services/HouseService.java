package com.example.housingmanagement.api.services;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HouseService {
    //TODO deal with Optionals - at the moment our Controllers check if the Optionals below would contain null and react
    //TODO with an appropriate ResponseEntity.

    @Autowired
    private HouseRepositoryJPA houseRepository;

    public List<HouseInternalEntity> fetchAll() {
        return houseRepository.findAll();
    }

    public void addHouseToDatabase(HouseInternalEntity houseInternalEntity) {
        houseRepository.save(houseInternalEntity);
    }

    @Transactional
    public void deleteHouseFromDatabase(HouseRequest houseRequest) {
        houseRepository.deleteByHouseNumber(houseRequest.getHouseNumber());
    }

    public HouseInternalEntity identifyHouseInternalEntity(HouseRequest houseRequest) {
        return houseRepository.findByHouseNumber(houseRequest.getHouseNumber());
    }

    public int houseCurrentCapacity(HouseRequest houseRequest) {
        return houseRepository.findByHouseNumber(houseRequest.getHouseNumber()).getCurrentCapacity();
    }

    public boolean existsByHouse(HouseRequest houseRequest) {
        return houseRepository.existsByHouseNumber(houseRequest.getHouseNumber());
    }

    public boolean houseHasSpareCapacity(HouseRequest houseRequest) {
        Optional<HouseInternalEntity> houseInternalEntityToBeChecked = Optional.ofNullable(houseRepository.findByHouseNumber(houseRequest.getHouseNumber()));

        return houseInternalEntityToBeChecked.get().getCurrentCapacity() < houseInternalEntityToBeChecked.get().getMaxCapacity();
    }

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


}
