package com.bowerzlabs.enums;

import lombok.Getter;

@Getter
public enum Industries {
    Adtech("Adtech"),
    Aerospace(""),
    Agriculture(""),
    Analytics("Analytics"),
    Biotech("Biotech"),
    Blockchain("Blockchain"),
    Community("Community"),
    Construction("Construction"),
    Developer_Tools("Developer Tools"),
    ECommerce("E-commerce"),
    Education("Education"),
    Energy("Energy"),
    Entertainment("Entertainment"),
    Finance("Finance"),
    Fitness("Fitness"),
    Food_Beverage("Food and Beverage"),
    Freight("Freight"),
    Gaming("Gaming"),
    Government("Government"),
    HardTech("Hard Tech"),
    Hardware("Hardware"),
    Healthcare("Healthcare"),
    Insurance("Insurance"),
    Marketplace("Marketplace"),
    Media("Media"),
    Others("Others"),
    Real_Estate("Real Estate"),
    Recruiting("Recruiting"),
    Retail("Retail"),
    Robotics("Robotics"),
    Saas("Saas"),
    Sales("Sales"),
    Security("Security"),
    Software("Software"),
    Sustainability("Sustainability"),
    Transportation("Transportation"),
    Tourism("Tourism"),
    VR_AR("Vr/AR"),
    Wellness("Wellness");

    private final String displayName;

    Industries(String displayName) {
        this.displayName = displayName;
    }

    public static Industries fromString(String value){
        for (Industries industry: Industries.values()){
            if (industry.getDisplayName().equalsIgnoreCase(value)){
                return industry;
            }
        }
        throw new IllegalArgumentException("Unknown company industry " + value);
    }

}
