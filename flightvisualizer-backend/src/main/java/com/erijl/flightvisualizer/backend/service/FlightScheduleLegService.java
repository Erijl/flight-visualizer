package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegWithDistance;
import com.erijl.flightvisualizer.backend.model.enums.LocationType;
import com.erijl.flightvisualizer.backend.model.repository.FlightScheduleLegRepository;
import com.erijl.flightvisualizer.backend.util.MathUtil;
import com.erijl.flightvisualizer.protos.filter.RouteFilter;
import com.erijl.flightvisualizer.protos.objects.Coordinate;
import com.erijl.flightvisualizer.protos.objects.LegRender;
import org.springframework.stereotype.Service;
import com.erijl.flightvisualizer.backend.model.entities.Airport;

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
                                MathUtil.calculateDistanceBetweenAirports(flightScheduleLegDto.getOriginAirport(), flightScheduleLegDto.getDestinationAirport())
                        )
                );
            }
        }
        return flightScheduleLegWithDistances;
    }

    public List<LegRender> getDistinctFlightScheduleLegsForRendering(RouteFilter routeFilter) {
        List<FlightScheduleLegDto> legs =  flightScheduleLegRepository.findDistinctFlightScheduleLegsByStartAndEndDate();
        List<LegRender> legRenders = new ArrayList<>();
        for (FlightScheduleLegDto leg : legs) {
            Airport originAirport = leg.getOriginAirport();
            Airport destinationAirport = leg.getDestinationAirport();

            var render = LegRender.newBuilder()
                    .setOriginAirportIataCode(originAirport.getIataAirportCode())
                    .setDestinationAirportIataCode(destinationAirport.getIataAirportCode())
                    .addCoordinates(0, Coordinate.newBuilder().setLatitude(originAirport.getLatitude().doubleValue()).setLongitude(originAirport.getLongitude().doubleValue()))
                    .addCoordinates(1, Coordinate.newBuilder().setLatitude(destinationAirport.getLatitude().doubleValue()).setLongitude(destinationAirport.getLongitude().doubleValue())).build();
            legRenders.add(render);
        }
        return legRenders;
    }
}
