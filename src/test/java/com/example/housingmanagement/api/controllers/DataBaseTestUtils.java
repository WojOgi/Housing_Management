package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.dbentities.Gender;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.requests.OccupantRequest;
import com.example.housingmanagement.api.responses.HouseResponse;
import com.example.housingmanagement.api.responses.OccupantResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
public class DataBaseTestUtils {

    private static final LocalDateTime now = LocalDateTime.now();

    private static final ObjectMapper objectMapper = new ObjectMapper();





    public static List<HouseResponse> getHouseResponseList(String responseContent) throws JsonProcessingException {
        return objectMapper.readValue(responseContent, new TypeReference<>() {
        });
    }

    public static HouseResponse getHouseResponse(String responseContent) throws JsonProcessingException {
        return objectMapper.readValue(responseContent, new TypeReference<>() {
        });
    }

    public static HouseRequest createValidHouseRequest(String houseNumber, int maxCapacity) {
        return new HouseRequest(houseNumber, maxCapacity);
    }

    public static HouseInternalEntity anEmptyHouse(String houseNumber) {
        return new HouseInternalEntity(now, houseNumber, 3, 0);
    }

    public static HouseInternalEntity aFullHouse(String houseNumber, int maxCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, maxCapacity);
    }

    public static HouseInternalEntity aPartiallyOccupiedHouse(String houseNumber, int maxCapacity, int currentCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, currentCapacity);
    }

    public static OccupantResponse getOccupantResponse(String responseContent) throws JsonProcessingException {
        return objectMapper.readValue(responseContent, new TypeReference<>() {
        });
    }

    public static List<OccupantResponse> getOccupantResponseList(String responseContent) throws JsonProcessingException {
        return objectMapper.readValue(responseContent, new TypeReference<>() {
        });
    }

    public static List<String> getLastNames(List<OccupantResponse> occupantResponses) {
        return occupantResponses.stream().map(OccupantResponse::getLastName).toList();
    }

    public static List<String> getFirstNames(List<OccupantResponse> occupantResponses) {
        return occupantResponses.stream().map(OccupantResponse::getFirstName).toList();
    }

    public static OccupantInternalEntity maleOccupant(String firstName, String lastName) {
        return new OccupantInternalEntity(now, firstName, lastName, Gender.MALE);
    }

    public static OccupantInternalEntity femaleOccupant(String firstName, String lastName) {
        return new OccupantInternalEntity(now, firstName, lastName, Gender.FEMALE);
    }

    public static OccupantRequest createValidOccupantRequest(String firstName, String lastName, Gender gender) {
        return new OccupantRequest(firstName, lastName, gender);
    }

    public static List<String> getListOfFirstNames(String responseContent) throws JsonProcessingException {
        return objectMapper.<List<OccupantResponse>>readValue(responseContent, new TypeReference<>() {
        }).stream().map(OccupantResponse::getFirstName).toList();
    }

    public static List<String> getListOfLastNames(String responseContent) throws JsonProcessingException {
        return objectMapper.<List<OccupantResponse>>readValue(responseContent, new TypeReference<>() {
        }).stream().map(OccupantResponse::getLastName).toList();
    }

    public static HouseRequest createValidHouseRequest(String houseNumber) {
        return new HouseRequest(houseNumber);
    }
}
