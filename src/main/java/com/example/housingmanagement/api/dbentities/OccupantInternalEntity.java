package com.example.housingmanagement.api.dbentities;


import jakarta.persistence.*;

@Entity
public class OccupantInternalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @ManyToOne
    @JoinColumn(name = "House Address")
    private HouseInternalEntity houseInternalEntity;

    public OccupantInternalEntity() {
    }

    public OccupantInternalEntity(String firstName, String lastName, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public OccupantInternalEntity(int id, String firstName, String lastName, Gender gender, HouseInternalEntity houseInternalEntity) {
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
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
