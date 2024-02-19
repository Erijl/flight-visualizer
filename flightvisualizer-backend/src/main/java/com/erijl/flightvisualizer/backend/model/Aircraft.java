package com.erijl.flightvisualizer.backend.model;

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
    @Column(name = "iata_aircraft_code")
    private String iataAircraftCode;

    @Column(name = "aircraft_name")
    private String aircraftName;
}