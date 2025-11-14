package com.kraftadmin.kraft_jpa_entities;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Embeddable
@Getter
@Setter
public class HardSkills {
    private List<String> mustHave;
    private List<String> niceToHave;
    private List<String> softSkills;
}
