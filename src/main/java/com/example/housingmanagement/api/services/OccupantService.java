package com.example.housingmanagement.api.services;

import com.example.housingmanagement.api.OccupantRepositoryJPA;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.responses.OccupantResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public Optional<OccupantInternalEntity> findById(Integer iD){
        return occupantRepository.findById(iD);
    }

    @Transactional
    public void deleteOccupantFromDatabase(OccupantRequest occupantRequest) {
        occupantRepository.deleteByFirstNameAndLastName(occupantRequest.getFirstName(), occupantRequest.getLastName());
    }


}
