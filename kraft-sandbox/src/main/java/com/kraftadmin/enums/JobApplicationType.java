package com.kraftadmin.enums;

public enum JobApplicationType {
    Internal,
    External,
    Managed;

    public static JobApplicationType fromString(String value){
        for (JobApplicationType jobApplicationType : JobApplicationType.values()){
            if (jobApplicationType.name().equalsIgnoreCase(value)){
                return jobApplicationType;
            }
        }

        throw new IllegalArgumentException("Unknown application type " + value);
    }
}
