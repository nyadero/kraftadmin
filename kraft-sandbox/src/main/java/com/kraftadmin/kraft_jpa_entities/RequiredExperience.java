package com.kraftadmin.kraft_jpa_entities;

import com.kraftadmin.enums.ExperienceLevel;
import com.kraftadmin.enums.Industries;
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
