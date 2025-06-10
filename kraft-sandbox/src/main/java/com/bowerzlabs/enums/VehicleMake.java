package com.bowerzlabs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VehicleMake {
    Abarth("Abarth"),
    Alfa_Romeo("Alfa Romeo"),
    Alpine("Alpine"),
    Aston_Martin("Aston Martin"),
    Audi("Audi"),
    Bentley("Bentley"),
    Bmw("Bmw"),
    Bugatti("Bugatti"),
    Cadillac("Cadillac"),
    Chevrolet("Chevrolet"),
    Dodge("Dodge"),
    Ferrari("Ferrari"),
    Ford("Ford"),
    Honda("Honda"),
    Hyundai("Hyundai"),
    Jaguar("Jaguar"),
    Lamborghini("Lamborghini"),
    LandRover("Land Rover"),
    Lotus("Lotus"),
    Maserati("Maserati"),
    Mazda("Mazda"),
    Mclaren("Mclaren"),
    Mercedes("Mercedes"),
    Mitsubishi("Mitsubishi"),
    Nissan("Nissan"),
    Peugeot("Peugeot"),
    Porsche("Porsche"),
    Subaru("Subaru"),
    Suzuki("Suzuki"),
    Toyota("Toyota"),
    Volkswagen("Volkswagen"),
    Volvo("Volvo"),
    Other("Other");

    private String displayName;

    public static VehicleMake fromString(String value){
        for (VehicleMake vehicleMake: VehicleMake.values()){
            if (vehicleMake.getDisplayName().equalsIgnoreCase(value)){
                return vehicleMake;
            }
        }
        throw new IllegalArgumentException("Unknown vehicle make  " + value);
    }
}
