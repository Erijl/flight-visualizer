package com.erijl.flightvisualizer.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "airport")

@Getter
@Setter
public class Airport {

    @Id
    @Column(name = "iata_airport_code")
    private String iataAirportCode;

    @Column(name = "airport_name")
    private String airportName;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "iata_city_code")
    private String iataCityCode;

    @Column(name = "iso_country_code")
    private String isoCountryCode;

    @Column(name = "location_type")
    private String locationType;

    @Column(name = "offset_utc")
    private Integer offsetUtc;

    @Column(name = "timezone_id")
    private String timezoneId;
}