package com.mjtech.pos.constant;

import java.util.ArrayList;
import java.util.List;

public enum Gender {
    MALE,
    FEMALE;

    public static List<String> getGenderStrings() {
        List<String> genderStrings = new ArrayList<>();
        for (Gender gender : Gender.values()) {
            genderStrings.add(gender.name());
        }
        return genderStrings;
    }
}
