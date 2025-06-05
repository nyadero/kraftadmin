package com.bowerzlabs.enums;

public enum JobApplicationStatus {
    New,
    Approved,
    Ignored,
    Cancelled,
    Withdrawn;

    public  static JobApplicationStatus fromString(String value){
        for(JobApplicationStatus jobApplicationStatus : JobApplicationStatus.values()){
            if(jobApplicationStatus.name().equalsIgnoreCase(value)){
                return jobApplicationStatus;
            }
        }

        throw new IllegalArgumentException("Unknown status: " + value);
    }
}
