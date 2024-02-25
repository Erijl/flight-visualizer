package com.erijl.flightvisualizer.backend.dto;

import com.erijl.flightvisualizer.backend.model.Airport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightScheduleLegWithDistance {

    private Integer legId;
    private Airport originAirport;
    private Airport destinationAirport;
    private int kilometerDistance;

    public FlightScheduleLegWithDistance(Integer legId, Airport originAirport, Airport destinationAirport, int kilometerDistance) {
        this.legId = legId;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.kilometerDistance = kilometerDistance;
    }

    public FlightScheduleLegWithDistance(FlightScheduleLegDto flightScheduleLegDto, int kilometerDistance) {
        this.legId = flightScheduleLegDto.getLegId();
        this.originAirport = flightScheduleLegDto.getOriginAirport();
        this.destinationAirport = flightScheduleLegDto.getDestinationAirport();
        this.kilometerDistance = kilometerDistance;
    }
}
