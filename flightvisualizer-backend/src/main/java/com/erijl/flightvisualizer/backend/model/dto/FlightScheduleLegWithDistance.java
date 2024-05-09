package com.erijl.flightvisualizer.backend.model.dto;

import com.erijl.flightvisualizer.backend.model.entities.Airport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightScheduleLegWithDistance {

    private Integer legId;
    private Integer flightScheduleId;
    private Airport originAirport;
    private Airport destinationAirport;
    private int aircraftDepartureTimeUtc;
    private int aircraftDepartureTimeDateDiffUtc;
    private int aircraftArrivalTimeUtc;
    private int aircraftArrivalTimeDateDiffUtc;
    private int kilometerDistance;


    public FlightScheduleLegWithDistance(FlightScheduleLegDto flightScheduleLegDto, int kilometerDistance) {
        this.legId = flightScheduleLegDto.getLegId();
        this.flightScheduleId = flightScheduleLegDto.getFlightScheduleId();
        this.originAirport = flightScheduleLegDto.getOriginAirport();
        this.destinationAirport = flightScheduleLegDto.getDestinationAirport();
        this.aircraftDepartureTimeUtc = flightScheduleLegDto.getAircraftDepartureTimeUtc();
        this.aircraftDepartureTimeDateDiffUtc = flightScheduleLegDto.getAircraftDepartureTimeDateDiffUtc();
        this.aircraftArrivalTimeUtc = flightScheduleLegDto.getAircraftArrivalTimeUtc();
        this.aircraftArrivalTimeDateDiffUtc = flightScheduleLegDto.getAircraftArrivalTimeDateDiffUtc();
        this.kilometerDistance = kilometerDistance;
    }
}
