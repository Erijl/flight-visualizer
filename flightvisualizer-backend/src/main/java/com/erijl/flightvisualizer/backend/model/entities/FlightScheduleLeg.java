package com.erijl.flightvisualizer.backend.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private Integer aircraftDepartureTimeUtc;

    @Column(name = "aircraft_departure_time_date_diff_utc")
    private Integer aircraftDepartureTimeDateDiffUtc;

    @Column(name = "aircraft_departure_time_lt")
    private Integer aircraftDepartureTimeLt;

    @Column(name = "aircraft_departure_time_diff_lt")
    private Integer aircraftDepartureTimeDiffLt;

    @Column(name = "aircraft_departure_time_variation")
    private Integer aircraftDepartureTimeVariation;

    @Column(name = "aircraft_arrival_time_utc")
    private Integer aircraftArrivalTimeUtc;

    @Column(name = "aircraft_arrival_time_date_diff_utc")
    private Integer aircraftArrivalTimeDateDiffUtc;

    @Column(name = "aircraft_arrival_time_lt")
    private Integer aircraftArrivalTimeLt;

    @Column(name = "aircraft_arrival_time_diff_lt")
    private Integer aircraftArrivalTimeDiffLt;

    @Column(name = "aircraft_arrival_time_variation")
    private Integer aircraftArrivalTimeVariation;

    public FlightScheduleLeg() {
    }

    public FlightScheduleLeg(FlightSchedule flightSchedule, Integer legSequenceNumber, Airport originAirport, Airport destinationAirport, String iataServiceTypeCode, Airline aircraftOwnerAirline, Aircraft aircraft, String aircraftConfigurationVersion, String registration, Boolean op, Integer aircraftDepartureTimeUtc, Integer aircraftDepartureTimeDateDiffUtc, Integer aircraftDepartureTimeLt, Integer aircraftDepartureTimeDiffLt, Integer aircraftDepartureTimeVariation, Integer aircraftArrivalTimeUtc, Integer aircraftArrivalTimeDateDiffUtc, Integer aircraftArrivalTimeLt, Integer aircraftArrivalTimeDiffLt, Integer aircraftArrivalTimeVariation) {
        this.flightSchedule = flightSchedule;
        this.legSequenceNumber = legSequenceNumber;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.iataServiceTypeCode = iataServiceTypeCode;
        this.aircraftOwnerAirline = aircraftOwnerAirline;
        this.aircraft = aircraft;
        this.aircraftConfigurationVersion = aircraftConfigurationVersion;
        this.registration = registration;
        this.op = op;
        this.aircraftDepartureTimeUtc = aircraftDepartureTimeUtc;
        this.aircraftDepartureTimeDateDiffUtc = aircraftDepartureTimeDateDiffUtc;
        this.aircraftDepartureTimeLt = aircraftDepartureTimeLt;
        this.aircraftDepartureTimeDiffLt = aircraftDepartureTimeDiffLt;
        this.aircraftDepartureTimeVariation = aircraftDepartureTimeVariation;
        this.aircraftArrivalTimeUtc = aircraftArrivalTimeUtc;
        this.aircraftArrivalTimeDateDiffUtc = aircraftArrivalTimeDateDiffUtc;
        this.aircraftArrivalTimeLt = aircraftArrivalTimeLt;
        this.aircraftArrivalTimeDiffLt = aircraftArrivalTimeDiffLt;
        this.aircraftArrivalTimeVariation = aircraftArrivalTimeVariation;
    }
}