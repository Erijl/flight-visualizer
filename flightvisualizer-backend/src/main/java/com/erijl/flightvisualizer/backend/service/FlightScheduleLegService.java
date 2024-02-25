package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.dto.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.dto.FlightScheduleLegWithDistance;
import com.erijl.flightvisualizer.backend.enums.LocationType;
import com.erijl.flightvisualizer.backend.repository.FlightScheduleLegRepository;
import org.springframework.stereotype.Service;
import com.erijl.flightvisualizer.backend.model.Airport;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlightScheduleLegService {

    private final FlightScheduleLegRepository flightScheduleLegRepository;

    public FlightScheduleLegService(FlightScheduleLegRepository flightScheduleLegRepository) {
        this.flightScheduleLegRepository = flightScheduleLegRepository;
    }

    public Iterable<FlightScheduleLegDto> getFlightScheduleLegs() {
        return flightScheduleLegRepository.findAllWithoutAssociations();
    }

    public List<FlightScheduleLegWithDistance> getFlightScheduleLegsWithDistance() {
        Iterable<FlightScheduleLegDto> flightScheduleLegDtos = flightScheduleLegRepository.findAllWithoutAssociations();

        List<FlightScheduleLegWithDistance> flightScheduleLegWithDistances = new ArrayList<>();
        for (FlightScheduleLegDto flightScheduleLegDto : flightScheduleLegDtos) {
            if(LocationType.AIRPORT.equalsName(flightScheduleLegDto.getOriginAirport().getLocationType()) &&
                    LocationType.AIRPORT.equalsName(flightScheduleLegDto.getDestinationAirport().getLocationType())) {
                flightScheduleLegWithDistances.add(
                        new FlightScheduleLegWithDistance(
                                flightScheduleLegDto,
                                this.calculateDistanceBetweenAirports(flightScheduleLegDto.getOriginAirport(), flightScheduleLegDto.getDestinationAirport())
                        )
                );
            }
        }
        return flightScheduleLegWithDistances;
    }

    private int calculateDistanceBetweenAirports(Airport originAirport, Airport destinationAirport) {

        if((originAirport == null || destinationAirport == null)
                || (originAirport.getLatitude() == null || originAirport.getLongitude() == null)
                || (destinationAirport.getLatitude() == null || destinationAirport.getLongitude() == null)) {
            return 0;
        }


        final double earthRadiusInMetres = 6371e3;

        double originLatitudeInRadians = Math.toRadians(originAirport.getLatitude().doubleValue());
        double destinationLatitudeInRadians = Math.toRadians(destinationAirport.getLatitude().doubleValue());
        double deltaLatitudeInRadians = Math.toRadians(destinationAirport.getLatitude().doubleValue() - originAirport.getLatitude().doubleValue());
        double deltaLongitudeInRadians = Math.toRadians(destinationAirport.getLongitude().doubleValue() - originAirport.getLongitude().doubleValue());

        double haversineFormulaPartA = Math.sin(deltaLatitudeInRadians/2) * Math.sin(deltaLatitudeInRadians/2) +
                Math.cos(originLatitudeInRadians) * Math.cos(destinationLatitudeInRadians) *
                        Math.sin(deltaLongitudeInRadians/2) * Math.sin(deltaLongitudeInRadians/2);
        double haversineFormulaPartC = 2 * Math.atan2(Math.sqrt(haversineFormulaPartA), Math.sqrt(1-haversineFormulaPartA));

        return ((int) Math.floor(earthRadiusInMetres * haversineFormulaPartC))/1000;
    }
}
