package com.example.housingmanagement.api.responses;

public class HouseResponse {

    private String houseNumber;

    public HouseResponse(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
}
