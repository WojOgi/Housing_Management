package com.example.housingmanagement.api.dbentities;

import jakarta.persistence.*;

@Entity
public class HouseInternalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int id;

    private String houseNumber;

    private int maxCapacity;
    private int currentOccupancy;

    public HouseInternalEntity(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public HouseInternalEntity() {
    }

    public HouseInternalEntity(int id, String houseNumber, int maxCapacity, int currentOccupancy) {
        this.id = id;
        this.houseNumber = houseNumber;
        this.maxCapacity = maxCapacity;
        this.currentOccupancy = currentOccupancy;
    }

    public HouseInternalEntity(String houseNumber, int maxCapacity, int currentOccupancy) {
        this.houseNumber = houseNumber;
        this.maxCapacity = maxCapacity;
        this.currentOccupancy = currentOccupancy;
    }

    public int getId() {
        return id;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(int currentCapacity) {
        this.currentOccupancy = currentCapacity;
    }

    @Override
    public String toString() {
        return houseNumber;
    }
}
