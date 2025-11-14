package com.kraftadmin.enums;

public enum JobStatus {
    Draft, Published, Closed;

    public static JobStatus fromString(String value){
        for (JobStatus jobStatus: JobStatus.values()){
            if (jobStatus.name().equalsIgnoreCase(value)){
                return jobStatus;
            }
        }
        throw new IllegalArgumentException("Unknown job status " + value);
    }
}
