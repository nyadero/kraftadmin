package com.bowerzlabs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum Category {
    SOFTWARE_ENGINEERING("Software Engineering", Arrays.asList(
            Specialization.SECURITY_CYBERSECURITY,
            Specialization.HARDWARE_ENGINEER,
            Specialization.COMPUTER_VISION_ENGINEER,
            Specialization.AR_VR_MR_ENGINEER,
            Specialization.BACKEND_ENGINEER,
            Specialization.BLOCKCHAIN_ENGINEER,
            Specialization.DATA_ENGINEER,
            Specialization.FULLSTACK_ENGINEER,
            Specialization.EMBEDDED_ENGINEER,
            Specialization.GAMING_ENGINEER,
            Specialization.MACHINE_LEARNING_ENGINEER,
            Specialization.MOBILE_ENGINEER,
            Specialization.NLP_ENGINEER,
            Specialization.FRONTEND_ENGINEER
    )),

    ENGINEERING_MANAGEMENT("Engineering Management", Arrays.asList(
            Specialization.APPLICATION_ENGINEERING_MANAGER,
            Specialization.MOBILE_ENGINEERING_MANAGER,
            Specialization.SEARCH_ENGINEERING_MANAGER,
            Specialization.MACHINE_LEARNING_MANAGER,
            Specialization.QA_MANAGER,
            Specialization.DATA_INFRASTRUCTURE_MANAGER,
            Specialization.DEVOPS_MANAGER
    )),

    DEVELOPER_OPERATIONS("Developer Operations", Arrays.asList(
            Specialization.DEVOPS_ENGINEER,
            Specialization.BUILD_RELEASE_ENGINEER,
            Specialization.SITE_RELIABILITY_ENGINEER
    )),

    DESIGN("Design", Arrays.asList(
            Specialization.UX_DESIGNER,
            Specialization.UX_RESEARCHER,
            Specialization.VISUAL_UI_DESIGNER,
            Specialization.PRODUCT_DESIGNER,
            Specialization.BRAND_GRAPHIC_DESIGNER
    )),

    PRODUCT_MANAGEMENT("Product Management", List.of(
            Specialization.PRODUCT_MANAGER
    )),

    SALES_AND_MARKETING("Sales and Marketing", Arrays.asList(
            Specialization.ACCOUNT_EXECUTIVE,
            Specialization.ACCOUNT_MANAGER,
            Specialization.BUSINESS_DEVELOPMENT,
            Specialization.SALES_MANAGER,
            Specialization.CUSTOMER_SUCCESS,
            Specialization.SALES_DEVELOPMENT_REP,
            Specialization.SALES_OPERATIONS
    )),

    DATA_ANALYTICS("Data Analytics", Arrays.asList(
            Specialization.DATA_SCIENTIST,
            Specialization.DATA_ANALYST,
            Specialization.BUSINESS_ANALYST,
            Specialization.BUSINESS_OPERATIONS
    )),

    QUALITY_ASSURANCE("Quality Assurance", Arrays.asList(
            Specialization.QA_TEST_AUTOMATION_ENGINEER,
            Specialization.QA_MANUAL_TEST_ENGINEER
            // Add other specializations for Quality Assurance...
    ));

//    ENGINEERING("Engineering", Arrays.asList(
//            Specialization.MECHANICAL_ENGINEER,
//            Specialization.ELECTRICAL_ENGINEER,
//            Specialization.CIVIL_ENGINEER,
//            Specialization.AEROSPACE_ENGINEER,
//            Specialization.BIOMEDICAL_ENGINEER
//    )),
//
//    ARTS("Arts", Arrays.asList(
//            Specialization.VISUAL_ARTIST,
//            Specialization.MULTIMEDIA_ARTIST,
//            Specialization.ANIMATION_ARTIST,
//            Specialization.ARCHITECT,
//            Specialization.INDUSTRIAL_DESIGNER
//    )),
//
//    MATHEMATICS("Mathematics", Arrays.asList(
//            Specialization.MATHEMATICIAN,
//            Specialization.STATISTICIAN,
//            Specialization.QUANTITATIVE_ANALYST,
//            Specialization.ACTUARY
//    )),
//
//    PHYSICS("Physics", Arrays.asList(
//            Specialization.THEORETICAL_PHYSICIST,
//            Specialization.EXPERIMENTAL_PHYSICIST,
//            Specialization.QUANTUM_PHYSICIST,
//            Specialization.ASTROPHYSICIST
//    ));


    private final String displayName;

    private final List<Specialization> specializations;

    public static Category fromString(String value){
        for (Category jobCategory : Category.values()){
            if(jobCategory.getDisplayName().equalsIgnoreCase(value)){
                System.out.println("job category " + jobCategory);
                return jobCategory;
            }
        }
        throw new IllegalArgumentException("Unknown job category " +  value );
    }
}
