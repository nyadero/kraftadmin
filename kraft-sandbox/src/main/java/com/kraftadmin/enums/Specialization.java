package com.kraftadmin.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum  Specialization {
    //    specializations for software engineering
    SECURITY_CYBERSECURITY("Security/Cyber security"),
    HARDWARE_ENGINEER("Hardware Engineer"),
    COMPUTER_VISION_ENGINEER("Computer Vision Engineer"),
    FRONTEND_ENGINEER("Frontend Engineer"),
    BACKEND_ENGINEER("Backend Engineer"),
    FULLSTACK_ENGINEER("Fullstack Engineer"),
    DATA_ENGINEER("Data Engineer"),
    EMBEDDED_ENGINEER("Embedded Engineer"),
    NLP_ENGINEER("Nlp Engineer"),
    MACHINE_LEARNING_ENGINEER("Machine Learning Engineer"),
    GAMING_ENGINEER("Gaming Engineer"),
    AR_VR_MR_ENGINEER("AI/VR/MR Engineer"),
    MOBILE_ENGINEER("Mobile Engineer"),
    BLOCKCHAIN_ENGINEER("Blockchain Engineer"),

    //   specialization for Engineering management
    APPLICATION_ENGINEERING_MANAGER("Application Engineering Manager"),
    MOBILE_ENGINEERING_MANAGER("Mobile Engineering Manager"),
    SEARCH_ENGINEERING_MANAGER("Search Engineering Manager"),
    MACHINE_LEARNING_MANAGER("Machine Learning Manager"),
    DATA_INFRASTRUCTURE_MANAGER("Data Infrastructure Manager"),
    DEVOPS_MANAGER("Devops Manager"),
    QA_MANAGER("QA Manager"),

    // Add other specializations for Developer Operations...
    DEVOPS_ENGINEER("DevOps Engineer"),
    BUILD_RELEASE_ENGINEER("Build/Release Engineer"),
    SITE_RELIABILITY_ENGINEER("Site Reliability Engineer"),

    //    Add  specializations for Design...
    UX_DESIGNER("UX Designer"),
    UX_RESEARCHER("UX Researcher"),
    VISUAL_UI_DESIGNER("Visual/UI Designer"),
    PRODUCT_DESIGNER("Product Designer"),
    BRAND_GRAPHIC_DESIGNER("Brand/Graphic Designer"),

    //    Add  specializations for Product Management...
    PRODUCT_MANAGER("Product Manager"),

    // Add  specializations for Sales and Marketing...
    ACCOUNT_EXECUTIVE("Account Executive"),
    ACCOUNT_MANAGER("Account Manager"),
    BUSINESS_DEVELOPMENT("Business Development"),
    SALES_MANAGER("Sales Manager"),
    CUSTOMER_SUCCESS("Customer Success"),
    SALES_DEVELOPMENT_REP("Sales Development Rep"),
    SALES_OPERATIONS("Sales Operations"),

    // Add  specializations for Data Analytics...
    DATA_SCIENTIST("Data Scientist"),
    DATA_ANALYST("Data Analyst"),
    BUSINESS_ANALYST("Business Analyst"),
    BUSINESS_OPERATIONS("Business Operations"),

    // Add  specializations for Quality Assurance...
    QA_TEST_AUTOMATION_ENGINEER("QA Test Automation Engineer"),
    QA_MANUAL_TEST_ENGINEER("QA Manual Test Engineer"),

    // Add specialization for Engineering jobs
    MECHANICAL_ENGINEER("Mechanical Engineer");


    private String displayName;

    Specialization(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Specialization fromString(String value){
        for (Specialization specialization: Specialization.values()){
            if(specialization.getDisplayName().equalsIgnoreCase(value)){
                return specialization;
            }
        }

        throw new IllegalArgumentException("Unknown job specialization " +  value );
    }

}
