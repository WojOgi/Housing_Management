package com.example.housingmanagement.api.mappers;

import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.requests.HouseRequest;
import com.example.housingmanagement.api.responses.HouseResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class HouseMapper implements HouseMapperInterface {

    @Override
    public List<HouseResponse> toHouseResponse(List<HouseInternalEntity> allHouseInternalEntities) {
        return Optional.ofNullable(allHouseInternalEntities).orElse(List.of()).stream()
                .filter(Objects::nonNull)
                .map(entity -> new HouseResponse(entity.getHouseNumber()))
                .toList();
        //tutaj piszemy co robimy - nie jak robimy
        //filtrujemy wszsytkie obiekty ktore nie sa nullami
        //wrzucam do optionala coś co może być nullem - to jest lista house int ent,
        //or else - jeśli to co wrzuciłem jest nullem to działąj na list of która jest pusta
        //ja sobie streamuje pustą listę. Czyli bronimy się przed nullem. Czyli możemy zwrócić pustą listę ale nie nulla
    }

    @Override
    public HouseInternalEntity toHouseInternalEntity(HouseRequest houseToBeAdded) {
        return new HouseInternalEntity(houseToBeAdded.getHouseNumber(),
                houseToBeAdded.getMaxCapacity(), 0);
    }
}
