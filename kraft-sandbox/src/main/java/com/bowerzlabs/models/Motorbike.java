package com.bowerzlabs.models;

import com.bowerzlabs.enums.MotorbikeCategory;
import com.bowerzlabs.enums.MotorbikeMake;
import com.bowerzlabs.enums.MotorbikeModel;
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
