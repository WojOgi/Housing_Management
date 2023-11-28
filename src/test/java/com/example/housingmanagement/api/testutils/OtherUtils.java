package com.example.housingmanagement.api.testutils;

import java.util.Random;

public class OtherUtils {

    public static String withValidHouseName() {
        String prefix = "G-";
        Random random = new Random();
        int suffix = random.nextInt(1000, 1999);
        String suffixString = Integer.toString(suffix);

        return prefix + suffixString;
    }
}
