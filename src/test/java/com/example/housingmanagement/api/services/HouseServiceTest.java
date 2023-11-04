package com.example.housingmanagement.api.services;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.requests.HouseRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class HouseServiceTest {

    @Mock
    private HouseRepositoryJPA houseRepository;

    private HouseService houseService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        houseService = new HouseService(houseRepository);
    }

    @Test
    @DisplayName("Tasting mocks :) When mock returns 'true' then our service returns 'truer")
    void test() {
        // given
        HouseRequest houseRequest = new HouseRequest("123");
        Mockito.when(houseRepository.existsByHouseNumber(houseRequest.getHouseNumber())).thenReturn(true);

        // when
        boolean result = houseService.existsByHouse(houseRequest);

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Tasting mocks :) When mock returns 'false' then our service returns 'false")
    void test2() {
        // given
        HouseRequest houseRequest = new HouseRequest("123");
        Mockito.when(houseRepository.existsByHouseNumber(houseRequest.getHouseNumber())).thenReturn(false);

        // when
        boolean result = houseService.existsByHouse(houseRequest);

        // then
        Assertions.assertFalse(result);
    }

}