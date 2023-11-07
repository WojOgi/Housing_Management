package com.example.housingmanagement.api.services;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HouseService {
    //TODO deal with Optionals - at the moment our Controllers check if the Optionals below would contain null and react
    //TODO with an appropriate ResponseEntity.
    private final HouseRepositoryJPA houseRepository;

    public HouseService(HouseRepositoryJPA houseRepository) {
        this.houseRepository = houseRepository;
    }

    public List<HouseInternalEntity> fetchAll() {
        return houseRepository.findAll();
    }

    public void addHouseToDatabase(HouseInternalEntity houseInternalEntity) {
        houseRepository.save(houseInternalEntity);
    }

    public Optional<HouseInternalEntity> findById(Integer iD) {
        return houseRepository.findById(iD);
    }

    @Transactional
    public void deleteHouseFromDatabase(HouseRequest houseRequest) {
        houseRepository.deleteByHouseNumber(houseRequest.getHouseNumber());
    }

    public int houseCurrentCapacity(HouseRequest houseRequest) {
        return houseRepository.findByHouseNumber(houseRequest.getHouseNumber()).getCurrentCapacity();
    }

    public boolean existsByHouse(HouseRequest houseRequest) {
        return houseRepository.existsByHouseNumber(houseRequest.getHouseNumber());
    }

    public boolean houseHasSpareCapacity(HouseRequest houseRequest) {
        HouseInternalEntity houseInternalEntityToBeChecked = houseRepository.findByHouseNumber(houseRequest.getHouseNumber());
        return houseInternalEntityToBeChecked.getCurrentCapacity() < houseInternalEntityToBeChecked.getMaxCapacity();
    }

    public void increaseHouseCurrentCapacityByOne(HouseRequest houseRequest) {

        HouseInternalEntity houseInternalEntityToIncreaseCapacityByOne = houseRepository.findByHouseNumber(houseRequest.getHouseNumber());

        HouseInternalEntity houseInternalEntity = new HouseInternalEntity(
                houseInternalEntityToIncreaseCapacityByOne.getId(),
                houseInternalEntityToIncreaseCapacityByOne.getCreated(),
                LocalDateTime.now(),
                houseInternalEntityToIncreaseCapacityByOne.getHouseNumber(),
                houseInternalEntityToIncreaseCapacityByOne.getMaxCapacity(),
                houseInternalEntityToIncreaseCapacityByOne.getCurrentCapacity() + 1
        );
        houseRepository.save(houseInternalEntity);
    }


    public void decreaseHouseCurrentCapacityByOne(HouseRequest houseRequest) {
        HouseInternalEntity houseInternalEntityToIncreaseCapacityByOne = houseRepository.findByHouseNumber(houseRequest.getHouseNumber());

        HouseInternalEntity houseInternalEntity = new HouseInternalEntity(
                houseInternalEntityToIncreaseCapacityByOne.getId(),
                houseInternalEntityToIncreaseCapacityByOne.getCreated(),
                LocalDateTime.now(),
                houseInternalEntityToIncreaseCapacityByOne.getHouseNumber(),
                houseInternalEntityToIncreaseCapacityByOne.getMaxCapacity(),
                houseInternalEntityToIncreaseCapacityByOne.getCurrentCapacity() - 1
        );
        houseRepository.save(houseInternalEntity);
    }
}
