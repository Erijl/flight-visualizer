package com.erijl.flightvisualizer.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "airline")

@Getter
@Setter
public class Airline {

    @Id
    @Column(name = "iata_airline_code")
    private String iataAirlineCode;

    @Column(name = "icao_airline_code")
    private String icaoAirlineCode;

    @Column(name = "airline_name")
    private String airlineName;
}
