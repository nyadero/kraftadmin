package com.bowerzlabs.models;

import com.bowerzlabs.enums.ExperienceLevel;
import com.bowerzlabs.enums.Industries;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.List;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequiredExperience {
    @Enumerated(EnumType.STRING)
    private ExperienceLevel seniority;
    private List<String> superTalents;
    @Enumerated(EnumType.STRING)
    private List<Industries> industry;
}
