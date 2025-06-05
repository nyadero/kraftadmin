package com.bowerzlabs.models;

import com.bowerzlabs.enums.Degree;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class EducationLevel {
    @Enumerated(EnumType.STRING)
    private Degree educationLevel;
    private String fieldOfStudy;
}
