package com.example.housingmanagement.api.services;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import org.junit.jupiter.api.*;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HouseServiceTest {

    @Mock
    private HouseRepositoryJPA houseRepository;

    private HouseService houseService;

    private final HouseRequest houseRequest = new HouseRequest("House1");

    private static final List<HouseInternalEntity> listWithoutNulls = new ArrayList<>();
    private static HouseInternalEntity houseInternalEntity = null;

    @BeforeAll
    static void beforeAll() {
        listWithoutNulls.add(new HouseInternalEntity(LocalDateTime.now(),"House1", 3, 0));
        listWithoutNulls.add(new HouseInternalEntity(LocalDateTime.now(),"House2", 2, 0));
        listWithoutNulls.add(new HouseInternalEntity(LocalDateTime.now(),"House3", 1, 0));

        houseInternalEntity = new HouseInternalEntity(LocalDateTime.now(),"House1", 3, 0);

    }

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        houseService = new HouseService(houseRepository);
    }

    @Test
    @DisplayName("When mock returns 'true' then our service returns 'true'")
    void test1(){
        //given
        Mockito.when(houseRepository.existsByHouseNumber(houseRequest.getHouseNumber())).thenReturn(true);

        //when
        boolean result = houseService.existsByHouse(houseRequest);

        //then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("When mock returns 'false' then our service returns 'false'")
    void test2(){
        //given
        Mockito.when(houseRepository.existsByHouseNumber(houseRequest.getHouseNumber())).thenReturn(false);

        //when
        boolean result = houseService.existsByHouse(houseRequest);

        //then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Should return a list of HouseInternalEntity after calling a method fetchAll()")
    void test3(){
        //given
        Mockito.when(houseRepository.findAll()).thenReturn(listWithoutNulls);

        //when
        List<HouseInternalEntity> houseInternalEntityList = houseService.fetchAll();

        //then
        Assertions.assertEquals(houseInternalEntityList, listWithoutNulls);
    }
    @Test
    @DisplayName("Should call a method save from houseRepository when method addHouseToDatabase is called")
    void test4(){
        //given
        HouseInternalEntity sampleHouse = houseInternalEntity;

        //when
        houseService.addHouseToDatabase(sampleHouse);

        //then
        Mockito.verify(houseRepository).save(sampleHouse);
    }
    @Test
    @DisplayName("Should return an Optional<HouseInternalEntity> when method findById is called")
    void test5(){
        //given
        Mockito.when(houseRepository.findById(0)).thenReturn(Optional.of(houseInternalEntity));

        //when
        Optional<HouseInternalEntity> sampleOptional = houseService.findById(0);

        //then
        Assertions.assertEquals(sampleOptional, Optional.of(houseInternalEntity));
    }

    @Test
    @DisplayName("Should call a method deleteByHouseNumber from houseRepository when deleteHouseFromDatabase is called")
    void test6(){
        //given
        HouseRequest sampleRequest = houseRequest;

        //when
        houseService.deleteHouseFromDatabase(sampleRequest);

        //then
        Mockito.verify(houseRepository).deleteByHouseNumber(houseRequest.getHouseNumber());
    }

    @Test
    @DisplayName("Should return a current capacity of HouseInternalEntity")
    void test7(){
        //given
        HouseRequest sampleRequest = houseRequest;
        HouseInternalEntity sampleHouseInternalEntity =
                new HouseInternalEntity(LocalDateTime.now(), "House",3,0);
        Mockito.when(houseRepository.findByHouseNumber(sampleRequest.getHouseNumber())).thenReturn(sampleHouseInternalEntity);

        //when
        int capacity = houseService.houseCurrentCapacity(sampleRequest);

        //then
        Mockito.verify(houseRepository).findByHouseNumber(sampleRequest.getHouseNumber());
        assertEquals(sampleHouseInternalEntity.getCurrentCapacity(),capacity); //here I am lost

    }


}