package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.dto.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.dto.FlightScheduleLegWithDistance;
import com.erijl.flightvisualizer.backend.enums.LocationType;
import com.erijl.flightvisualizer.backend.repository.FlightScheduleLegRepository;
import org.springframework.stereotype.Service;
import com.erijl.flightvisualizer.backend.model.Airport;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightScheduleLegService {

    private final FlightScheduleLegRepository flightScheduleLegRepository;

    public FlightScheduleLegService(FlightScheduleLegRepository flightScheduleLegRepository) {
        this.flightScheduleLegRepository = flightScheduleLegRepository;
    }

    public Iterable<FlightScheduleLegDto> getFlightScheduleLegs(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if(!startDate.isEmpty() && !endDate.isEmpty()) {
            return flightScheduleLegRepository.findAllWithoutAssociationsByStartAndEndDate(Date.valueOf(LocalDate.parse(startDate, formatter)), Date.valueOf(LocalDate.parse(endDate, formatter)));
        } else if(!startDate.isEmpty()) {
            return flightScheduleLegRepository.findAllWithoutAssociationsBySingleDate(Date.valueOf(LocalDate.parse(startDate, formatter)));
        } else {
            return flightScheduleLegRepository.findAllWithoutAssociationsBySingleDate(Date.valueOf(LocalDate.parse(endDate, formatter)));
        }
    }

    public List<FlightScheduleLegWithDistance> getFlightScheduleLegsWithDistance(String startDate, String endDate) {

        Iterable<FlightScheduleLegDto> flightScheduleLegDtos = this.getFlightScheduleLegs(startDate, endDate);

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
