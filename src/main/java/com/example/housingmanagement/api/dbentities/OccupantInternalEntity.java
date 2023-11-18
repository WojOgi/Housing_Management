package com.example.housingmanagement.api.dbentities;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class OccupantInternalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "Created")
    private LocalDateTime created;
    @Column(name = "Updated")
    private LocalDateTime updated;
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

    public OccupantInternalEntity(LocalDateTime created, String firstName, String lastName, Gender gender) {
        this.created = created;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public OccupantInternalEntity(LocalDateTime created, String firstName, String lastName, Gender gender, HouseInternalEntity houseInternalEntity) {
        this.created = created;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.houseInternalEntity = houseInternalEntity;
    }

    public OccupantInternalEntity(int id, LocalDateTime created, LocalDateTime updated, String firstName, String lastName,
                                  Gender gender, HouseInternalEntity houseInternalEntity) {
        this.id = id;
        this.created = created;
        this.updated = updated;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.houseInternalEntity = houseInternalEntity;
    }

    public int getId() {
        return id;
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
