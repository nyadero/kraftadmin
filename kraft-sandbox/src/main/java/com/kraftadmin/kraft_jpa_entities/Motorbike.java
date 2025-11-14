package com.kraftadmin.kraft_jpa_entities;

import com.kraftadmin.enums.MotorbikeCategory;
import com.kraftadmin.enums.MotorbikeMake;
import com.kraftadmin.enums.MotorbikeModel;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "motorbike_garages")
public class Motorbike extends Garage {
    @Enumerated(EnumType.STRING)
    private MotorbikeMake motorbikeMake;

    @Enumerated(EnumType.STRING)
    private MotorbikeModel motorbikeModel;

    @Enumerated(EnumType.STRING)
    private MotorbikeCategory motorbikeCategory;
}
