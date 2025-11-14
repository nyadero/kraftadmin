package com.kraftadmin.kraft_jpa_entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kraftadmin.annotations.KraftAdminField;
import com.kraftadmin.enums.EngineAspiration;
import com.kraftadmin.enums.FuelType;
import com.kraftadmin.enums.GarageCategory;
import com.kraftadmin.enums.TransmissionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "garages")
@Inheritance(strategy = InheritanceType.JOINED)
public class Garage{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private double buyingPrice;

    private int previousOwnersCount;

    private int mileage;

    private double acceleration;

    private int topSpeed;

    private int enginePower;

    private int torque;

    @KraftAdminField(editable = false)
    private int commentsCount;

    @KraftAdminField(editable = false)
    private int likesCount;

    @Enumerated(EnumType.STRING)
    private TransmissionType transmissionType;

    @Column(insertable=false, updatable=false)
    @Enumerated(EnumType.STRING)
    private GarageCategory category;

    @Enumerated(value = EnumType.STRING)
    private FuelType fuelType;

    @Enumerated(value = EnumType.STRING)
    private EngineAspiration engineAspiration;

//    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
//    @JoinColumn(name = "userId", foreignKey = @ForeignKey(name = "FK_USER_ID"))
//    @JsonManagedReference
//    private User user;
//
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "garage")
//    private List<UploadedFile> garageFiles = new ArrayList<>(0);
//
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "garage")
//    @OrderBy(value = "createdAt DESC")
//    private List<Comment> comments = new ArrayList<>();

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}