package com.example.housingmanagement.api.dbentities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class HouseInternalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "Created")
    private LocalDateTime created;

    @Column(name = "Updated")
    private LocalDateTime updated;

    @Column(name = "House Number")
    private String houseNumber;
    @Column(name = "Maximum Capacity")
    private int maxCapacity;
    @Column(name = "Current Capacity")
    private int currentCapacity;

    public HouseInternalEntity() {
    }


    public HouseInternalEntity(LocalDateTime created, String houseNumber, int maxCapacity, int currentCapacity) {
        this.created = created;
        this.houseNumber = houseNumber;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
    }

    public HouseInternalEntity(int id, LocalDateTime created, LocalDateTime updated, String houseNumber, int maxCapacity, int currentCapacity) {
        this.id = id;
        this.created = created;
        this.updated = updated;
        this.houseNumber = houseNumber;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime dateCreated) {
        this.created = dateCreated;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime dateUpdated) {
        this.updated = dateUpdated;
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
