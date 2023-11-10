package com.example.housingmanagement.api.services;

import com.example.housingmanagement.api.HouseRepositoryJPA;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HouseServiceTest {

    @Mock
    private HouseRepositoryJPA houseRepositoryMock;
    @InjectMocks
    private HouseService houseService;

    private LocalDateTime now = LocalDateTime.now();


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("When mock returns 'true' then our service returns 'true'")
    void test1() {
        //given
        HouseRequest sampleHouseRequest = new HouseRequest("House1");
        Mockito.when(houseRepositoryMock.existsByHouseNumber(sampleHouseRequest.getHouseNumber())).thenReturn(true);

        //when
        boolean result = houseService.existsByHouse(sampleHouseRequest);

        //then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("When mock returns 'false' then our service returns 'false'")
    void test2() {
        //given
        HouseRequest sampleHouseRequest = new HouseRequest("House1");
        Mockito.when(houseRepositoryMock.existsByHouseNumber(sampleHouseRequest.getHouseNumber())).thenReturn(false);

        //when
        boolean result = houseService.existsByHouse(sampleHouseRequest);

        //then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Should return a list of HouseInternalEntity after calling a method fetchAll()")
    void test3() {
        //given
        List<HouseInternalEntity> listWithoutNulls = List.of(
                new HouseInternalEntity(now, "House1", 3, 0));
        Mockito.when(houseRepositoryMock.findAll()).thenReturn(listWithoutNulls);

        //when
        List<HouseInternalEntity> houseInternalEntityList = houseService.fetchAll();

        //then
        Assertions.assertEquals(houseInternalEntityList, listWithoutNulls);
    }

    @Test
    @DisplayName("Should call a method save from houseRepository when method addHouseToDatabase is called")
    void test4() {
        //given
        HouseInternalEntity sampleHouse = new HouseInternalEntity(now, "House1", 3, 0);

        //when
        houseService.addHouseToDatabase(sampleHouse);

        //then
        Mockito.verify(houseRepositoryMock).save(sampleHouse);
    }

    @Test
    @DisplayName("Should return an Optional<HouseInternalEntity> when method findById is called")
    void test5() {
        //given
        HouseInternalEntity sampleHouseInternalEntity =
                new HouseInternalEntity(now, "House1", 3, 0);
        Mockito.when(houseRepositoryMock.findById(0)).thenReturn(Optional.of(sampleHouseInternalEntity));

        //when
        Optional<HouseInternalEntity> result = houseService.findById(0);

        //then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get(), sampleHouseInternalEntity);
        Assertions.assertEquals(result, Optional.of(sampleHouseInternalEntity));
    }

    @Test
    @DisplayName("Should call a method deleteByHouseNumber from houseRepository when deleteHouseFromDatabase is called")
    void test6() {
        //given
        HouseRequest sampleHouseRequest = new HouseRequest("House1");

        //when
        houseService.deleteHouseFromDatabase(sampleHouseRequest);

        //then
        Mockito.verify(houseRepositoryMock).deleteByHouseNumber(sampleHouseRequest.getHouseNumber());
    }

    @Test
    @DisplayName("Should return a current capacity of HouseInternalEntity")
    void test7() {
        //given
        HouseRequest sampleHouseRequest = new HouseRequest("House1");
        HouseInternalEntity sampleHouseInternalEntity =
                new HouseInternalEntity(now, "House1", 3, 0);
        Mockito.when(houseRepositoryMock
                .findByHouseNumber(sampleHouseRequest.getHouseNumber())).thenReturn(sampleHouseInternalEntity);

        //when
        int capacity = houseService.houseCurrentCapacity(sampleHouseRequest);

        //then
        Mockito.verify(houseRepositoryMock).findByHouseNumber(sampleHouseRequest.getHouseNumber());
        assertEquals(sampleHouseInternalEntity.getCurrentCapacity(), capacity);
    }
    @Test
    @DisplayName("Should return true if HouseInternalEntity exists in db")
    void test8(){
        //given
        HouseRequest sampleHouseRequest = new HouseRequest("House1");
        Mockito.when(houseRepositoryMock.existsByHouseNumber(sampleHouseRequest.getHouseNumber())).thenReturn(true);

        //when
        boolean exists = houseService.existsByHouse(sampleHouseRequest);

        //then
        Assertions.assertTrue(exists);
    }
    @Test
    @DisplayName("Should return false if HouseInternalEntity does not exist in db")
    void test9(){
        //given
        HouseRequest sampleHouseRequest = new HouseRequest("House1");
        Mockito.when(houseRepositoryMock.existsByHouseNumber(sampleHouseRequest.getHouseNumber())).thenReturn(false);

        //when
        boolean exists = houseService.existsByHouse(sampleHouseRequest);

        //then
        Assertions.assertFalse(exists);
    }
    @Test
    @DisplayName("Should return true if HouseInternalEntity has spare capacity")
    void test10(){
        //given
        HouseRequest sampleHouseRequest = new HouseRequest("House1");
        HouseInternalEntity sampleHouseInternalEntity =
                new HouseInternalEntity(now, "House1", 3, 0);
        Mockito.when(houseRepositoryMock
                .findByHouseNumber(sampleHouseRequest.getHouseNumber())).thenReturn(sampleHouseInternalEntity);

        //when
        boolean hasSpareCapacity = houseService.houseHasSpareCapacity(sampleHouseRequest);

        //then
        Assertions.assertTrue(hasSpareCapacity);
    }

    @Test
    @DisplayName("Should increase HouseInternalEntity capacity by one based on HouseRequest")
    void test11() {
        //given
        HouseRequest sampleHouseRequest = new HouseRequest("House1");
        HouseInternalEntity foundHouseInternalEntity = new HouseInternalEntity(1, LocalDateTime.now(), LocalDateTime.now(),
                "House1", 3, 0);

        Mockito.when(houseRepositoryMock.findByHouseNumber(sampleHouseRequest.getHouseNumber())).thenReturn(foundHouseInternalEntity);
        ArgumentCaptor<HouseInternalEntity> captor = ArgumentCaptor.forClass(HouseInternalEntity.class);

        //when
        houseService.increaseHouseCurrentCapacityByOne(sampleHouseRequest);

        //then
        Mockito.verify(houseRepositoryMock).findByHouseNumber(sampleHouseRequest.getHouseNumber());
        Mockito.verify(houseRepositoryMock).save(captor.capture());
        HouseInternalEntity updatedHouseInternalEntity = captor.getValue();
        assertEquals(foundHouseInternalEntity.getCurrentCapacity()+1,updatedHouseInternalEntity.getCurrentCapacity());
    }
    @Test
    @DisplayName("Should decrease HouseInternalEntity capacity by one based on HouseRequest")
    void test12() {
        //given
        HouseRequest sampleHouseRequest = new HouseRequest("House1");
        HouseInternalEntity foundHouseInternalEntity = new HouseInternalEntity(1, LocalDateTime.now(), LocalDateTime.now(),
                "House1", 3, 0);

        Mockito.when(houseRepositoryMock.findByHouseNumber(sampleHouseRequest.getHouseNumber())).thenReturn(foundHouseInternalEntity);
        ArgumentCaptor<HouseInternalEntity> captor = ArgumentCaptor.forClass(HouseInternalEntity.class);

        //when
        houseService.decreaseHouseCurrentCapacityByOne(sampleHouseRequest);

        //then
        Mockito.verify(houseRepositoryMock).findByHouseNumber(sampleHouseRequest.getHouseNumber());
        Mockito.verify(houseRepositoryMock).save(captor.capture());
        HouseInternalEntity updatedHouseInternalEntity = captor.getValue();
        assertEquals(foundHouseInternalEntity.getCurrentCapacity()-1,updatedHouseInternalEntity.getCurrentCapacity());
    }





}