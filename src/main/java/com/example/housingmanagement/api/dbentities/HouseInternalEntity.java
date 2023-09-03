package com.example.housingmanagement.api.dbentities;

import jakarta.persistence.*;

@Entity
public class HouseInternalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String houseNumber;
    private int maxCapacity;
    private int currentCapacity;

    public HouseInternalEntity(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public HouseInternalEntity() {
    }

    public HouseInternalEntity(int id, String houseNumber, int maxCapacity, int currentCapacity) {
        this.id = id;
        this.houseNumber = houseNumber;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
    }

    public HouseInternalEntity(String houseNumber, int maxCapacity, int currentCapacity) {
        this.houseNumber = houseNumber;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
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

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    @Override
    public String toString() {
        return houseNumber;
    }
}
