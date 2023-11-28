package com.example.housingmanagement.api.testutils;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.OccupantRepositoryJPA;
import com.example.housingmanagement.api.dbentities.Gender;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class DataBaseTestUtils {

    private static OccupantRepositoryJPA occupantRepository;


    private static HouseRepositoryJPA houseRepository;

    @Autowired
    private DataBaseTestUtils(OccupantRepositoryJPA occupantRepository, HouseRepositoryJPA houseRepository) {
        DataBaseTestUtils.houseRepository = houseRepository;
        DataBaseTestUtils.occupantRepository = occupantRepository;
    }

    public static boolean isExistsByHouseNumber(String houseNumber) {
        return houseRepository.existsByHouseNumber(houseNumber);
    }

    public static OccupantInternalEntity getOccupantByFirstNameAndLastName(String firstName, String lastName) {
        return occupantRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    public static boolean occupantRepositoryIsEmpty() {
        return occupantRepository.findAll().isEmpty();
    }

    public static boolean houseRepositoryIsEmpty() {
        return houseRepository.findAll().isEmpty();
    }

    public static HouseInternalEntity getUpdatedHouse(String houseNumber) {
        return houseRepository.findByHouseNumber(houseNumber);
    }

    public static OccupantInternalEntity getUpdatedOccupant(String firstName, String lastName) {
        return occupantRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    public static void putIntoHouseDatabase(HouseInternalEntity houseInternalEntity) {
        houseRepository.save(houseInternalEntity);
    }

    public static void putIntoHouseDatabase(HouseInternalEntity houseInternalEntity1, HouseInternalEntity houseInternalEntity2) {
        houseRepository.saveAll(List.of(houseInternalEntity1, houseInternalEntity2));
    }

    public static void putIntoOccupantDatabase(OccupantInternalEntity occupantInternalEntity) {
        occupantRepository.save(occupantInternalEntity);
    }

    public static List<String> getFirstNames() {
        return occupantRepository.findAll().stream().map(OccupantInternalEntity::getFirstName)
                .toList();
    }

    public static void clearHouseRepository() {
        houseRepository.deleteAll();
    }

    public static void clearOccupantRepository() {
        occupantRepository.deleteAll();
    }

    public static void putIntoOccupantDatabase(OccupantInternalEntity occupant1, OccupantInternalEntity occupant2) {
        occupantRepository.saveAll(List.of(occupant1, occupant2));
    }

    public static String getHouseNumber(int index) {
        return houseRepository.findAll().get(index).getHouseNumber();
    }

    public static int getIdForHouse(String houseNumber) {
        return houseRepository.findByHouseNumber(houseNumber).getId();
    }

    public static int getIdByFirstAndLastName(String firstName, String lastName) {
        return occupantRepository.findByFirstNameAndLastName(firstName, lastName).getId();
    }

    public static int getOccupantRepositorySize() {
        return occupantRepository.findAll().size();
    }

    public static Gender getGenderByIndex(int index) {
        return occupantRepository.findAll().get(index).getGender();
    }

    public static String getFirstNameByIndex(int index) {
        return occupantRepository.findAll().get(index).getFirstName();
    }

    public static String getLastNameByIndex(int index) {
        return occupantRepository.findAll().get(index).getLastName();
    }
}
