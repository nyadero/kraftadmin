package com.bowerzlabs.models;

import com.bowerzlabs.enums.JobApplicationType;
import com.bowerzlabs.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "jobs")
@Builder
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "specialization")
    @Enumerated(EnumType.STRING)
    private Specialization specialization;

    private List<String> languages;

    private String title;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    private String location;

    @Embedded
    private RequiredExperience experience;

    @Embedded
    private HardSkills skills;

    @Embedded
    private EducationLevel educationLevel;

    private Double salary;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private JobApplicationType jobApplicationType;

    @OneToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(name = "talent_id")
    private List<Talent> applicants;

    @Enumerated(EnumType.STRING)
    private JobPromoter jobPromoter;

    @Enumerated(EnumType.STRING)
    private WorkModel workModel;

    @Transient
    private JobMatch jobMatch;

    @Transient
    private JobMatchResults jobMatchResults;

    @Enumerated(EnumType.STRING)
    private JobStatus jobStatus;

    private int viewsCount;

    private int applicationsCount;

    private Double salaryExpectation;


    @Transient
    private Boolean isApplied = false;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private LocalDate recruitmentEnds;

//    public void calculateAndSetJobMatchResults(Talent talent) {
//        if (talent != null) {
//            this.jobMatchResults = JobMatchCalculatorUtil.calculateJobMatchCriteria(talent, this);
//        }
//    }
}
