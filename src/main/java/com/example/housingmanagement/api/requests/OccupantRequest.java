package com.example.housingmanagement.api.requests;

import com.example.housingmanagement.api.dbentities.Gender;

public class OccupantRequest {
    private String firstName;
    private String lastName;

    private final Gender gender;

    public OccupantRequest(String firstName, String lastName, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
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
}
