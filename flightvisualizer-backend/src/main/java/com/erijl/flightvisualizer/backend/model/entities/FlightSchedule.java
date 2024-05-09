package com.erijl.flightvisualizer.backend.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "flight_schedule")

@Getter
@Setter
public class FlightSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_schedule_id")
    private Integer flightScheduleId;

    @ManyToOne
    @JoinColumn(name = "airline_code", referencedColumnName = "iata_airline_code")
    private Airline airline;

    @ManyToOne
    @JoinColumn(name = "operation_period_id", referencedColumnName = "operation_period_id")
    private FlightScheduleOperationPeriod operationPeriod;

    @Column(name = "flight_number")
    private Integer flightNumber;

    @Column(name = "suffix")
    private String suffix;

    public FlightSchedule() {
    }

    public FlightSchedule(Airline airline, FlightScheduleOperationPeriod operationPeriod, Integer flightNumber, String suffix) {
        this.airline = airline;
        this.operationPeriod = operationPeriod;
        this.flightNumber = flightNumber;
        this.suffix = suffix;
    }
}