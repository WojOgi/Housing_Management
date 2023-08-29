package com.example.housingmanagement.api;


import jakarta.persistence.*;

@Entity
public class OccupantInternalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int id;
    private String firstName;
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "house Address")
    private HouseInternalEntity houseInternalEntity;

    public OccupantInternalEntity() {
    }

    public OccupantInternalEntity(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public OccupantInternalEntity(int id, String firstName, String lastName, HouseInternalEntity houseInternalEntity) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.houseInternalEntity = houseInternalEntity;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public HouseInternalEntity getHouseInternalEntity() {
        return houseInternalEntity;
    }

    public void setHouseInternalEntity(HouseInternalEntity houseInternalEntity) {
        this.houseInternalEntity = houseInternalEntity;
    }


}
