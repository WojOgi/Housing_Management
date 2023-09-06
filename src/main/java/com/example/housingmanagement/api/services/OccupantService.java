package com.example.housingmanagement.api.services;

import com.example.housingmanagement.api.OccupantRepositoryJPA;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.OccupantRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OccupantService {
    @Autowired
    private OccupantRepositoryJPA occupantRepository;

    public List<OccupantInternalEntity> fetchAll(){
        return occupantRepository.findAll();
    }
    public boolean existsByOccupant(OccupantRequest occupantRequest) {

        return occupantRepository.existsByFirstNameAndLastName(
                occupantRequest.getFirstName(), occupantRequest.getLastName());
    }
    public String retrieveOccupantGenderFromRequest(OccupantRequest occupantRequest) {
        return occupantRequest.getGender();
    }
    public String retrieveOccupantGenderFromInternalEntity(OccupantInternalEntity occupantInternalEntity) {
        return occupantInternalEntity.getGender();
    }
    public void addOccupantToDatabase(OccupantInternalEntity occupantInternalEntity) {
        occupantRepository.save(occupantInternalEntity);
    }
    @Transactional
    public void deleteOccupantFromDatabase(OccupantRequest occupantRequest){
        occupantRepository.deleteByFirstNameAndLastName(occupantRequest.getFirstName(),occupantRequest.getLastName());
    }




}
