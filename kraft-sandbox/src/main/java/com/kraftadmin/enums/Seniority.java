package com.kraftadmin.enums;

public enum Seniority {
    Junior, Mid, Senior;

    public static Seniority fromString(String value){
        for (Seniority seniority: Seniority.values()){
            if(seniority.name().equalsIgnoreCase(value)){
                return seniority;
            }
        }

        throw new IllegalArgumentException("Unknown seniority level " + value);
    }
}
