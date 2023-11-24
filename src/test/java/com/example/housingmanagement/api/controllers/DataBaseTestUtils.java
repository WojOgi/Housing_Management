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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Component
public class DataBaseTestUtils {

    private static final LocalDateTime now = LocalDateTime.now();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static MockMvc mockMvc;

    @Autowired
    public DataBaseTestUtils(MockMvc mockMvc) {
        DataBaseTestUtils.mockMvc = mockMvc;
    }

    public static String performGet(String url) throws Exception {
        return mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }


    public static String performGetWithId(String url, int id) throws Exception {
        return mockMvc.perform(get(url, id).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    public static MvcResult getMvcResultOfPOST(HouseRequest houseRequest, String url, ResultMatcher expectedResult) throws Exception {
        return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(houseRequest))).andExpect(expectedResult).andReturn();
    }

    public static MvcResult getMvcResultOfDELETE(HouseRequest houseRequest, String url, ResultMatcher expectedResult) throws Exception {
        return mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(houseRequest))).andExpect(expectedResult).andReturn();
    }

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

    public static HouseInternalEntity anEmptyHouse(String houseNumber, int maxCapacity) {
        return new HouseInternalEntity(now, houseNumber, maxCapacity, 0);
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
