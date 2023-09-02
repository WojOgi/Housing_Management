package com.example.housingmanagement.api.requests;

import java.util.Objects;

public class OccupantRequest {
    private String firstName;
    private String lastName;

    private final String gender;

    public OccupantRequest(String firstName, String lastName, String gender) {
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

    public String getGender() {
        return gender;
    }
}
