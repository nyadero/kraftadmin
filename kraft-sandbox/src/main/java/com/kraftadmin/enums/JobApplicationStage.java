package com.kraftadmin.enums;

public enum JobApplicationStage {
    Applied,
    Rejected,
    Evaluation,
    Hired,
    Interview,
    Offer;

    public static JobApplicationStage fromString(String value){
        for (JobApplicationStage jobApplicationStage : JobApplicationStage.values()){
            if(jobApplicationStage.name().equalsIgnoreCase(value)){
                return jobApplicationStage;
            }
        }

        throw new IllegalArgumentException("Unknown application stage: " + value);
    }
}
