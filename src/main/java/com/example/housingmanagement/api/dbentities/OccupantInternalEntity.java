package com.example.housingmanagement.api.dbentities;


import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import jakarta.persistence.*;

@Entity
public class OccupantInternalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String firstName;
    private String lastName;
    private String gender;
    @ManyToOne
    @JoinColumn(name = "house Address")
    private HouseInternalEntity houseInternalEntity;

    public OccupantInternalEntity() {
    }

    public OccupantInternalEntity(String firstName, String lastName, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public OccupantInternalEntity(int id, String firstName, String lastName, String gender, HouseInternalEntity houseInternalEntity) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public HouseInternalEntity getHouseInternalEntity() {
        return houseInternalEntity;
    }

    public void setHouseInternalEntity(HouseInternalEntity houseInternalEntity) {
        this.houseInternalEntity = houseInternalEntity;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " " + gender;
    }
}
