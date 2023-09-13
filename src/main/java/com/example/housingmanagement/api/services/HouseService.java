package com.example.housingmanagement.api.services;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Optional<HouseInternalEntity> houseInternalEntityToBeChecked =
                Optional.ofNullable(houseRepository.findByHouseNumber(houseRequest.getHouseNumber()));

        return houseInternalEntityToBeChecked
                .filter(houseInternalEntity -> houseInternalEntity.getCurrentCapacity() < houseInternalEntity.getMaxCapacity()).isPresent();
    }

    public void increaseHouseCurrentCapacityByOne(HouseRequest houseRequest) {

        Optional<HouseInternalEntity> houseInternalEntityToIncreaseCapacityByOne =
                Optional.ofNullable(houseRepository.findByHouseNumber(houseRequest.getHouseNumber()));

        houseInternalEntityToIncreaseCapacityByOne
                .ifPresent(houseInternalEntity -> houseInternalEntity.setCurrentCapacity(houseInternalEntity.getCurrentCapacity() + 1));

        if (houseInternalEntityToIncreaseCapacityByOne.isPresent()) {
            HouseInternalEntity houseInternalEntity = new HouseInternalEntity(
                    houseInternalEntityToIncreaseCapacityByOne.get().getId(),
                    houseInternalEntityToIncreaseCapacityByOne.get().getHouseNumber(),
                    houseInternalEntityToIncreaseCapacityByOne.get().getMaxCapacity(),
                    houseInternalEntityToIncreaseCapacityByOne.get().getCurrentCapacity());

            houseRepository.save(houseInternalEntity);
        }
    }

    public void decreaseHouseCurrentCapacityByOne(HouseRequest houseRequest) {

        Optional<HouseInternalEntity> houseInternalEntityToDecreaseCapacityByOne =
                Optional.ofNullable(houseRepository.findByHouseNumber(houseRequest.getHouseNumber()));

        houseInternalEntityToDecreaseCapacityByOne
                .ifPresent(houseInternalEntity -> houseInternalEntity.setCurrentCapacity(houseInternalEntity.getCurrentCapacity() + 1));

        if (houseInternalEntityToDecreaseCapacityByOne.isPresent()) {

            HouseInternalEntity houseInternalEntity = new HouseInternalEntity(
                    houseInternalEntityToDecreaseCapacityByOne.get().getId(),
                    houseInternalEntityToDecreaseCapacityByOne.get().getHouseNumber(),
                    houseInternalEntityToDecreaseCapacityByOne.get().getMaxCapacity(),
                    houseInternalEntityToDecreaseCapacityByOne.get().getCurrentCapacity());

            houseRepository.save(houseInternalEntity);
        }
    }
}
