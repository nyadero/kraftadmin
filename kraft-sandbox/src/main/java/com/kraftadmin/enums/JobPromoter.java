package com.kraftadmin.enums;

public enum JobPromoter {
    Individual, Company;

    public static JobPromoter fromString (String value){
        for (JobPromoter jobPromoter: JobPromoter.values()){
            if (jobPromoter.name().equalsIgnoreCase(value)){
                return jobPromoter;
            }
        }

        throw new IllegalArgumentException("unknown job promoter " + value);
    }
}
