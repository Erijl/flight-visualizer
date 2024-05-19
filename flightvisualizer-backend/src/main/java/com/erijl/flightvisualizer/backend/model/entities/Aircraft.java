package com.erijl.flightvisualizer.backend.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "aircraft")

@Getter
@Setter
public class Aircraft {

    @Id
    @Column(name = "id")
    private String iataAircraftCode;

    @Column(name = "aircraft_name")
    private String aircraftName;

    public Aircraft(String iataAircraftCode, String aircraftName) {
        this.iataAircraftCode = iataAircraftCode;
        this.aircraftName = aircraftName;
    }

    public Aircraft(String iataAircraftCode) {
        this.iataAircraftCode = iataAircraftCode;
    }

    public Aircraft(){

    }
}