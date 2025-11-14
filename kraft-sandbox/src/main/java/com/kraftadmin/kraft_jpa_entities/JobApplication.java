package com.kraftadmin.kraft_jpa_entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kraftadmin.enums.JobApplicationStage;
import com.kraftadmin.enums.JobApplicationStatus;
import com.kraftadmin.enums.JobMatch;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "talent_id")
    private Talent talent;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "job_id")
    @JsonIgnore
    private Job job;

    @OneToOne(
            cascade = CascadeType.ALL,
            mappedBy = "jobApplication",
            orphanRemoval = true
    )
    private Interview interview;

//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinColumn(name = "offer_id", nullable = false)
//    private List<JobOffer> jobOffers;

    // status of application, New by default
    @Enumerated(EnumType.STRING)
    private JobApplicationStatus jobApplicationStatus = JobApplicationStatus.New;

    @Enumerated(EnumType.STRING)
    private JobApplicationStage jobApplicationStage = JobApplicationStage.Evaluation;

    @Enumerated(EnumType.STRING)
    private JobMatch jobMatch;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appliedOn;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
