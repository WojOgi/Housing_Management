package com.example.housingmanagement.api.dbentities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class HouseInternalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private String houseNumber;
    private int maxCapacity;
    private int currentCapacity;

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

    public HouseInternalEntity(LocalDateTime dateCreated, String houseNumber, int maxCapacity, int currentCapacity) {
        this.dateCreated = dateCreated;
        this.houseNumber = houseNumber;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
    }

    public HouseInternalEntity(int id, LocalDateTime dateCreated, String houseNumber, int maxCapacity, int currentCapacity) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.houseNumber = houseNumber;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
    }

    public HouseInternalEntity(int id, LocalDateTime dateCreated, LocalDateTime dateUpdated, String houseNumber, int maxCapacity, int currentCapacity) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.houseNumber = houseNumber;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
