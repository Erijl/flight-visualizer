package com.erijl.flightvisualizer.backend.dto;

import com.erijl.flightvisualizer.backend.model.Airport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightScheduleLegDto {
    private Integer legId;
    private Airport originAirport;
    private Airport destinationAirport;

    public FlightScheduleLegDto(Integer legId, Airport originAirport, Airport destinationAirport) {
        this.legId = legId;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
    }
}