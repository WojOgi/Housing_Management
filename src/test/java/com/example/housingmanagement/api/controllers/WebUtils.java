package com.example.housingmanagement.api.controllers;

import com.example.housingmanagement.api.requests.HouseRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class WebUtils {

    private static MockMvc mockMvc;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public WebUtils(MockMvc mockMvc) {
        WebUtils.mockMvc = mockMvc;
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
}
