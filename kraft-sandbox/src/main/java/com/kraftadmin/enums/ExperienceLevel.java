package com.kraftadmin.enums;

public enum ExperienceLevel {
    Beginner, Junior, Intermediate, Advanced, Experienced, Senior;

    public static ExperienceLevel fromString(String value){
        for (ExperienceLevel experienceLevel: ExperienceLevel.values()){
            if (experienceLevel.name().equalsIgnoreCase(value)){
                return experienceLevel;
            }
        }
        throw new IllegalArgumentException("Unknown experience level " + value);
    }
}
