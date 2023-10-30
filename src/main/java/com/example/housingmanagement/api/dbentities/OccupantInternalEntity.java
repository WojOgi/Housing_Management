package com.example.housingmanagement.api.dbentities;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class OccupantInternalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "Date Created")
    private LocalDateTime dateCreated;
    @Column(name = "Date Updated")
    private LocalDateTime dateUpdated;
    @Column(name = "First Name")
    private String firstName;
    @Column(name = "Last Name")
    private String lastName;
    @Enumerated(EnumType.STRING)
    @Column(name = "Gender")
    private Gender gender;
    @ManyToOne
    @JoinColumn(name = "House Assigned")
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

    public OccupantInternalEntity(LocalDateTime dateCreated, String firstName, String lastName, Gender gender) {
        this.dateCreated = dateCreated;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public OccupantInternalEntity(int id, LocalDateTime dateCreated, String firstName, String lastName,
                                  Gender gender, HouseInternalEntity houseInternalEntity) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.houseInternalEntity = houseInternalEntity;
    }

    public OccupantInternalEntity(int id, LocalDateTime dateCreated, LocalDateTime dateUpdated, String firstName, String lastName,
                                  Gender gender, HouseInternalEntity houseInternalEntity) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.houseInternalEntity = houseInternalEntity;
    }

    public int getId() {
        return id;
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
