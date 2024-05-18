package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegWithDistance;
import com.erijl.flightvisualizer.backend.model.enums.LocationType;
import com.erijl.flightvisualizer.backend.model.projections.LegRenderDataProjection;
import com.erijl.flightvisualizer.backend.model.repository.FlightScheduleLegRepository;
import com.erijl.flightvisualizer.backend.util.CustomTimeUtil;
import com.erijl.flightvisualizer.backend.util.MathUtil;
import com.erijl.flightvisualizer.protos.objects.Coordinate;
import com.erijl.flightvisualizer.protos.objects.LegRender;
import com.erijl.flightvisualizer.protos.objects.TimeFilter;
import org.springframework.stereotype.Service;

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

    public List<LegRender> getDistinctFlightScheduleLegsForRendering(TimeFilter timeFilter) {
        //TODO possible error when selecting too many days (check db performance)
        LocalDate startDate = CustomTimeUtil.convertProtoTimestampToLocalDate(timeFilter.getDateRange().getStart());
        LocalDate endDate = CustomTimeUtil.convertProtoTimestampToLocalDate(timeFilter.getDateRange().getEnd());
        List<LegRenderDataProjection> legs =  flightScheduleLegRepository.findDistinctFlightScheduleLegsByStartAndEndDate(startDate, endDate, timeFilter.getTimeRange().getStart(), timeFilter.getTimeRange().getEnd());

        List<LegRender> legRenders = new ArrayList<>();
        for (LegRenderDataProjection legData : legs) {
            var render = LegRender.newBuilder()
                    .setOriginAirportIataCode(legData.getOriginAirportIataCode())
                    .setDestinationAirportIataCode(legData.getDestinationAirportIataCode())
                    .addCoordinates(0, Coordinate.newBuilder().setLatitude(legData.getOriginLatitude()).setLongitude(legData.getOriginLongitude()))
                    .addCoordinates(1, Coordinate.newBuilder().setLatitude(legData.getDestinationLatitude()).setLongitude(legData.getDestinationLongitude())).build();
            legRenders.add(render);
        }
        return legRenders;
    }
}
