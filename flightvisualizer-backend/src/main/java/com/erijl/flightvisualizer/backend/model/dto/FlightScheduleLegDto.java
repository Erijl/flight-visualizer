package com.erijl.flightvisualizer.backend.model.dto;

import com.erijl.flightvisualizer.backend.model.entities.Airport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightScheduleLegDto {
    private Integer legId;
    private Integer flightScheduleId;
    private Airport originAirport;
    private Airport destinationAirport;
    private int aircraftDepartureTimeUtc;
    private int aircraftDepartureTimeDateDiffUtc;
    private int aircraftArrivalTimeUtc;
    private int aircraftArrivalTimeDateDiffUtc;


    public FlightScheduleLegDto(Integer legId, Integer flightScheduleId, Airport originAirport,
                                Airport destinationAirport, int aircraftDepartureTimeUtc,
                                int aircraftDepartureTimeDateDiffUtc, int aircraftArrivalTimeUtc,
                                int aircraftArrivalTimeDateDiffUtc) {
        this.legId = legId;
        this.flightScheduleId = flightScheduleId;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.aircraftDepartureTimeUtc = aircraftDepartureTimeUtc;
        this.aircraftDepartureTimeDateDiffUtc = aircraftDepartureTimeDateDiffUtc;
        this.aircraftArrivalTimeUtc = aircraftArrivalTimeUtc;
        this.aircraftArrivalTimeDateDiffUtc = aircraftArrivalTimeDateDiffUtc;
    }
}