package com.erijl.flightvisualizer.backend.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "flight_schedule_cron_run")

@Getter
@Setter
public class FlightScheduleCronRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer cronRunId;

    @Column(name = "cron_run_date_utc")
    private String cronRunDateUtc;

    @Column(name = "cron_run_finish")
    private Timestamp cronRunFinish;

    @Column(name = "aircraft_count")
    private Integer aircraftCount;

    @Column(name = "airline_count")
    private Integer airlineCount;

    @Column(name = "airport_count")
    private Integer airportCount;

    @Column(name = "flight_schedule_count")
    private Integer flightScheduleCount;
}