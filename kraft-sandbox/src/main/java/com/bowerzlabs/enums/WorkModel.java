package com.bowerzlabs.enums;

public enum WorkModel {
    Onsite, Hybrid, Remote;

    public static  WorkModel fromString(String value){
        for (WorkModel workModel: WorkModel.values()){
            if (workModel.name().equalsIgnoreCase(value)){
                return  workModel;
            }
        }
        throw new IllegalArgumentException("Unknown work model :"  + value);
    }
}
