package com.example.housingmanagement.api.services;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.OccupantRepositoryJPA;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.requests.OccupantRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssignmentService {
    //TODO deal with Optionals
    private final HouseRepositoryJPA houseRepository;

    private final OccupantRepositoryJPA occupantRepository;

    public AssignmentService(HouseRepositoryJPA houseRepository, OccupantRepositoryJPA occupantRepository) {
        this.houseRepository = houseRepository;
        this.occupantRepository = occupantRepository;
    }

    public List<OccupantInternalEntity> getOccupantsAssignedToThisHouseIntEnt(HouseInternalEntity houseInternalEntity) {
        if (Objects.isNull(houseInternalEntity)) {
            return List.of();
        }
        return occupantRepository.findAll().stream()
                .filter(it -> it.getHouseInternalEntity() != null)
                .filter(it -> it.getHouseInternalEntity().getHouseNumber().equals(houseInternalEntity.getHouseNumber()))
                .collect(Collectors.toList());
    }

    public HouseInternalEntity houseCurrentlyAssignedToThisOccupant(OccupantRequest occupantRequest) {
        return Optional.ofNullable(occupantRepository.findByFirstNameAndLastName(occupantRequest.getFirstName(), occupantRequest.getLastName()))
                .filter(it -> Objects.nonNull(it.getHouseInternalEntity()))
                .map(OccupantInternalEntity::getHouseInternalEntity)
                .orElse(null);
    }

    public void assignSpecificOccupantToSpecificHouse(HouseRequest houseRequest, OccupantRequest occupantRequest) {
        //identify House
        Optional<HouseInternalEntity> houseInternalEntityToAssign =
                Optional.ofNullable(houseRepository.findByHouseNumber(houseRequest.getHouseNumber()));
        //identify Occupant
        Optional<OccupantInternalEntity> occupantInternalEntityToAssign =
                Optional.ofNullable(occupantRepository.findByFirstNameAndLastName(occupantRequest.getFirstName(),
                        occupantRequest.getLastName()));
        //update database entry for the identified occupant
        //potentially method to be extracted
        if (occupantInternalEntityToAssign.isPresent() && houseInternalEntityToAssign.isPresent()) {
            OccupantInternalEntity occupantInternalEntity = occupantInternalEntityToAssign.get();
            OccupantInternalEntity occupantInternalEntityToBeModified =
                    new OccupantInternalEntity(
                            occupantInternalEntity.getId(),
                            occupantInternalEntity.getDateCreated(),
                            LocalDateTime.now(),
                            occupantInternalEntity.getFirstName(),
                            occupantInternalEntity.getLastName(),
                            occupantInternalEntity.getGender(),
                            houseInternalEntityToAssign.get());
            occupantRepository.save(occupantInternalEntityToBeModified);
        }
    }
}
