package com.example.housingmanagement.api;

import java.util.List;
import java.util.Optional;

public interface HousingDatabaseInterface {

    List<HouseInternalEntity> getAllHouses();

    List<OccupantInternalEntity> getAllOccupants();


    void addHouseToDatabase(HouseInternalEntity houseInternalEntity);

    void deleteHouseFromDatabase(HouseRequest houseRequest);


    int identifyHouseInDatabaseByAddressFromRequest(HouseRequest houseRequest);

    Optional<HouseInternalEntity> identifiedHouseInDatabase(int houseId);

    List<OccupantInternalEntity> getOccupantsAssignedToThisHouseIntEnt(Optional<HouseInternalEntity> houseInternalEntity);

    int houseCurrentCapacity(HouseRequest houseRequest);

    boolean existsByHouse(HouseRequest houseRequest);


    int identifyOccupantByItsFirstAndLastName(OccupantRequest occupantRequest);

    boolean existsByOccupant(OccupantRequest occupantRequest);

    String retrieveOccupantGenderFromRequest(OccupantRequest occupantRequest);

    String retrieveOccupantGenderFromInternalEntity(OccupantInternalEntity occupantInternalEntity);


    void addOccupantToDatabase(OccupantInternalEntity occupantInternalEntity);

    void deleteOccupantFromDatabase(OccupantRequest occupantRequest);


    boolean houseHasSpareCapacity(HouseRequest houseRequest);

    HouseInternalEntity houseCurrentlyAssignedToThisOccupant(OccupantRequest occupantRequest);

    void increaseHouseCurrentCapacityByOne(HouseRequest houseRequest);

    void decreaseHouseCurrentCapacityByOne(HouseRequest houseRequest);


    void assignSpecificOccupantToSpecificHouse(HouseRequest houseRequest, OccupantRequest occupantRequest);
    //void removeSpecificOccupantFromItsCurrentHouse(OccupantRequest occupantRequest);


}
