package com.example.housingmanagement;

import com.example.housingmanagement.api.dbentities.Gender;
import com.example.housingmanagement.api.dbentities.HouseInternalEntity;
import com.example.housingmanagement.api.dbentities.OccupantInternalEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyCommandLineRunner implements CommandLineRunner {
    final HouseRepoCLR houseRepoCLR;
    final OccupantRepoCLR occupantRepoCLR;

    @Autowired
    public MyCommandLineRunner(HouseRepoCLR houseRepoCLR, OccupantRepoCLR occupantRepoCLR) {
        this.houseRepoCLR = houseRepoCLR;
        this.occupantRepoCLR = occupantRepoCLR;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Populating the database ...");

        HouseInternalEntity house1 = new HouseInternalEntity(LocalDateTime.now(), "G-1119", 3, 0);
        houseRepoCLR.save(house1);
        System.out.println(("Added: " + house1));

        HouseInternalEntity house2 = new HouseInternalEntity(LocalDateTime.now(), "G-1229", 2, 0);
        houseRepoCLR.save(house2);
        System.out.println(("Added: " + house2));

        HouseInternalEntity house3 = new HouseInternalEntity(LocalDateTime.now(), "G-1339", 4, 0);
        houseRepoCLR.save(house3);
        System.out.println(("Added: " + house3));

        HouseInternalEntity house4 = new HouseInternalEntity(LocalDateTime.now(), "G-1449", 1, 0);
        houseRepoCLR.save(house4);
        System.out.println(("Added: " + house4));

        HouseInternalEntity house5 = new HouseInternalEntity(LocalDateTime.now(), "G-1559", 2, 0);
        houseRepoCLR.save(house5);
        System.out.println(("Added: " + house5));

        OccupantInternalEntity occupant1 = new OccupantInternalEntity(LocalDateTime.now(), "John", "Smith", Gender.MALE);
        occupantRepoCLR.save(occupant1);
        System.out.println(("Added: " + occupant1));

        OccupantInternalEntity occupant2 = new OccupantInternalEntity(LocalDateTime.now(), "Barry", "White", Gender.MALE);
        occupantRepoCLR.save(occupant2);
        System.out.println(("Added: " + occupant2));

        OccupantInternalEntity occupant3 = new OccupantInternalEntity(LocalDateTime.now(), "Marry", "Black", Gender.FEMALE);
        occupantRepoCLR.save(occupant3);
        System.out.println(("Added: " + occupant3));

        OccupantInternalEntity occupant4 = new OccupantInternalEntity(LocalDateTime.now(), "Kitty", "Cox", Gender.FEMALE);
        occupantRepoCLR.save(occupant4);
        System.out.println(("Added: " + occupant4));


    }
}

@Component
interface HouseRepoCLR extends JpaRepository<HouseInternalEntity, Integer> {
}

@Component
interface OccupantRepoCLR extends JpaRepository<OccupantInternalEntity, Integer> {
}
