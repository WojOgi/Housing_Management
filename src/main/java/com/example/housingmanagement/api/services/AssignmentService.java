package com.example.housingmanagement.api.services;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.OccupantRepositoryJPA;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.requests.OccupantRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {
    //TODO deal with Optionals
    private final HouseRepositoryJPA houseRepository;

    private final OccupantRepositoryJPA occupantRepository;

    public AssignmentService(HouseRepositoryJPA houseRepository, OccupantRepositoryJPA occupantRepository) {
        this.houseRepository = houseRepository;
        this.occupantRepository = occupantRepository;
    }

    public List<OccupantInternalEntity> getOccupantsAssignedToThisHouseIntEnt(Optional<HouseInternalEntity> houseInternalEntity) {
        List<OccupantInternalEntity> allOccupants = occupantRepository.findAll();

        List<OccupantInternalEntity> occupantsWithAnyHouseAssigned =
                allOccupants.stream().filter(x -> x.getHouseInternalEntity() != null).toList();

        return occupantsWithAnyHouseAssigned.stream().filter(x -> x.getHouseInternalEntity().getHouseNumber()
                .equals(houseInternalEntity.get().getHouseNumber())).toList();

    }

    public HouseInternalEntity houseCurrentlyAssignedToThisOccupant(OccupantRequest occupantRequest) {
        Optional<OccupantInternalEntity> occupantToCheckIfHasHouseAssigned =
                Optional.ofNullable(occupantRepository.findByFirstNameAndLastName(occupantRequest.getFirstName(),
                        occupantRequest.getLastName()));

        if (occupantToCheckIfHasHouseAssigned.get().getHouseInternalEntity() == null) {
            return null;
        }
        return occupantToCheckIfHasHouseAssigned.get().getHouseInternalEntity();
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
