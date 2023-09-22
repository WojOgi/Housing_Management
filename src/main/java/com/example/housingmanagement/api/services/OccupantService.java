package com.example.housingmanagement.api.services;

import com.example.housingmanagement.api.OccupantRepositoryJPA;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.OccupantRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OccupantService {
    private final OccupantRepositoryJPA occupantRepository;

    public OccupantService(OccupantRepositoryJPA occupantRepository) {
        this.occupantRepository = occupantRepository;
    }

    public List<OccupantInternalEntity> fetchAll() {
        return occupantRepository.findAll();
    }

    public boolean existsByOccupant(OccupantRequest occupantRequest) {

        return occupantRepository.existsByFirstNameAndLastName(
                occupantRequest.getFirstName(), occupantRequest.getLastName());
    }

    public void addOccupantToDatabase(OccupantInternalEntity occupantInternalEntity) {
        occupantRepository.save(occupantInternalEntity);
    }

    @Transactional
    public void deleteOccupantFromDatabase(OccupantRequest occupantRequest) {
        occupantRepository.deleteByFirstNameAndLastName(occupantRequest.getFirstName(), occupantRequest.getLastName());
    }


}
