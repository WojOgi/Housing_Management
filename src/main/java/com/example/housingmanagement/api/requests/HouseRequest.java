package com.example.housingmanagement.api.requests;

import java.util.Objects;

public class HouseRequest {
    private String houseNumber;
    private int maxCapacity;

    public HouseRequest(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public HouseRequest(String houseNumber, int maxCapacity) {
        this.houseNumber = houseNumber;
        this.maxCapacity = maxCapacity;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HouseRequest that = (HouseRequest) o;
        return maxCapacity == that.maxCapacity && houseNumber.equals(that.houseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(houseNumber, maxCapacity);
    }

    @Override
    public String toString() {
        return houseNumber;
    }
}
