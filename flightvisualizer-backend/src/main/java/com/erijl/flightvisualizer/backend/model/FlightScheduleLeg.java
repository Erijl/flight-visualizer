package com.erijl.flightvisualizer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Entity
@Table(name = "flight_schedule_leg")

@Getter
@Setter
public class FlightScheduleLeg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leg_id")
    private Integer legId;

    @ManyToOne
    @JoinColumn(name = "flight_schedule_id", referencedColumnName = "flight_schedule_id")
    private FlightSchedule flightSchedule;

    @Column(name = "leg_sequence_number")
    private Integer legSequenceNumber;

    @ManyToOne
    @JoinColumn(name = "origin_airport", referencedColumnName = "iata_airport_code")
    private Airport originAirport;

    @ManyToOne
    @JoinColumn(name = "destination_airport", referencedColumnName = "iata_airport_code")
    private Airport destinationAirport;

    @Column(name = "iata_service_type_code")
    private String iataServiceTypeCode;

    @ManyToOne
    @JoinColumn(name = "aircraft_owner_airline_code", referencedColumnName = "iata_airline_code")
    private Airline aircraftOwnerAirline;

    @ManyToOne
    @JoinColumn(name = "aircraft_code", referencedColumnName = "iata_aircraft_code")
    private Aircraft aircraft;

    @Column(name = "aircraft_configuration_version")
    private String aircraftConfigurationVersion;

    @Column(name = "registration")
    private String registration;

    @Column(name = "op")
    private Boolean op;

    @Column(name = "aircraft_departure_time_utc")
    private Time aircraftDepartureTimeUtc;

    @Column(name = "aircraft_departure_time_date_diff_utc")
    private Integer aircraftDepartureTimeDateDiffUtc;

    @Column(name = "aircraft_departure_time_lt")
    private Time aircraftDepartureTimeLt;

    @Column(name = "aircraft_departure_time_diff_lt")
    private Integer aircraftDepartureTimeDiffLt;

    @Column(name = "aircraft_departure_time_variation")
    private String aircraftDepartureTimeVariation;

    @Column(name = "aircraft_arrival_time_utc")
    private Time aircraftArrivalTimeUtc;

    @Column(name = "aircraft_arrival_time_date_diff_utc")
    private Integer aircraftArrivalTimeDateDiffUtc;

    @Column(name = "aircraft_arrival_time_lt")
    private Time aircraftArrivalTimeLt;

    @Column(name = "aircraft_arrival_time_diff_lt")
    private Integer aircraftArrivalTimeDiffLt;

    @Column(name = "aircraft_arrival_time_variation")
    private String aircraftArrivalTimeVariation;
}