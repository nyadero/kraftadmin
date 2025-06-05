package com.bowerzlabs.enums;

public enum EmploymentType {
    Fulltime, Parttime, Internship, Freelance, Contract, Consultancy, Volunteer;

    public static EmploymentType fromString(String value){
        for (EmploymentType employmentType: EmploymentType.values()){
            if (employmentType.name().equalsIgnoreCase(value)){
                return employmentType;
            }
        }
        throw new IllegalArgumentException("Unknown employment type " + value);
    }
}


