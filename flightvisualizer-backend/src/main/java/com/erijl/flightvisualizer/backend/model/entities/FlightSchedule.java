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
    @Column(name = "id")
    private Integer flightScheduleId;

    @ManyToOne
    @JoinColumn(name = "airline_code", referencedColumnName = "id")
    private Airline airline;

    @ManyToOne
    @JoinColumn(name = "operation_period_id", referencedColumnName = "id")
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