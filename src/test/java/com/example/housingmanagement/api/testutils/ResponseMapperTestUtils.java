package com.example.housingmanagement.api.testutils;

import com.example.housingmanagement.api.responses.HouseResponse;
import com.example.housingmanagement.api.responses.OccupantResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ResponseMapperTestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<HouseResponse> getHouseResponseList(String responseContent) throws JsonProcessingException {
        return objectMapper.readValue(responseContent, new TypeReference<>() {
        });
    }

    public static HouseResponse getHouseResponse(String responseContent) throws JsonProcessingException {
        return objectMapper.readValue(responseContent, new TypeReference<>() {
        });
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

    public static List<String> getListOfFirstNames(String responseContent) throws JsonProcessingException {
        return objectMapper.<List<OccupantResponse>>readValue(responseContent, new TypeReference<>() {
        }).stream().map(OccupantResponse::getFirstName).toList();
    }

    public static List<String> getListOfLastNames(String responseContent) throws JsonProcessingException {
        return objectMapper.<List<OccupantResponse>>readValue(responseContent, new TypeReference<>() {
        }).stream().map(OccupantResponse::getLastName).toList();
    }
}
