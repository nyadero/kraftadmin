package com.kraftadmin.enums;

public enum JobMatch {
    Unknown, Poor, Fair, Average, Good, Perfect;

    public static JobMatch fromString(String value){
        for(JobMatch jobMatch: JobMatch.values()){
            if (jobMatch.name().equalsIgnoreCase(value)){
                return jobMatch;
            }
        }

        throw new IllegalArgumentException("Unknown job match " + value);
    }
}
