package com.kraftadmin.enums;

public enum Degree {
    Bootcamp,
    Certificate,
    Diploma,
    Bachelors,
    Masters,
    Phd;
    public static Degree fromString(String degree){
        for (Degree degree1 : Degree.values()){
            if (degree1.name().equalsIgnoreCase(degree)){
                return degree1;
            }
        }
        throw new IllegalArgumentException("Unknown degree " + degree);
    }
}
