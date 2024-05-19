package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.builder.LegRenderBuilder;
import com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegWithDistance;
import com.erijl.flightvisualizer.backend.model.enums.LocationType;
import com.erijl.flightvisualizer.backend.model.projections.LegRenderDataProjection;
import com.erijl.flightvisualizer.backend.model.repository.FlightScheduleLegRepository;
import com.erijl.flightvisualizer.backend.util.CustomTimeUtil;
import com.erijl.flightvisualizer.backend.util.FilterUtil;
import com.erijl.flightvisualizer.backend.util.MathUtil;
import com.erijl.flightvisualizer.backend.validators.*;
import com.erijl.flightvisualizer.protos.dtos.SandboxModeResponseObject;
import com.erijl.flightvisualizer.protos.filter.CombinedFilterRequest;
import com.erijl.flightvisualizer.protos.objects.AirportRender;
import com.erijl.flightvisualizer.protos.objects.LegRender;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FlightScheduleLegService {

    private final FlightScheduleLegRepository flightScheduleLegRepository;
    private final AirportService airportService;

    public FlightScheduleLegService(FlightScheduleLegRepository flightScheduleLegRepository, AirportService airportService) {
        this.flightScheduleLegRepository = flightScheduleLegRepository;
        this.airportService = airportService;
    }

    public Iterable<FlightScheduleLegDto> getFlightScheduleLegs(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            return flightScheduleLegRepository.findAllWithoutAssociationsByStartAndEndDate(Date.valueOf(LocalDate.parse(startDate, formatter)), Date.valueOf(LocalDate.parse(endDate, formatter)));
        } else if (!startDate.isEmpty()) {
            return flightScheduleLegRepository.findAllWithoutAssociationsBySingleDate(Date.valueOf(LocalDate.parse(startDate, formatter)));
        } else {
            return flightScheduleLegRepository.findAllWithoutAssociationsBySingleDate(Date.valueOf(LocalDate.parse(endDate, formatter)));
        }
    }

    public List<FlightScheduleLegWithDistance> getFlightScheduleLegsWithDistance(String startDate, String endDate) {

        Iterable<FlightScheduleLegDto> flightScheduleLegDtos = this.getFlightScheduleLegs(startDate, endDate);

        List<FlightScheduleLegWithDistance> flightScheduleLegWithDistances = new ArrayList<>();
        for (FlightScheduleLegDto flightScheduleLegDto : flightScheduleLegDtos) {
            if (LocationType.AIRPORT.equalsName(flightScheduleLegDto.getOriginAirport().getLocationType()) &&
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

    public SandboxModeResponseObject getDistinctFlightScheduleLegsForRendering(CombinedFilterRequest combinedFilterRequest) {
        CombinedFilterRequestValidator.validate(combinedFilterRequest);

        //TODO possible error when selecting too many days (check db performance)
        LocalDate startDate = CustomTimeUtil.convertProtoTimestampToLocalDate(combinedFilterRequest.getTimeFilter().getDateRange().getStart());
        LocalDate endDate = CustomTimeUtil.convertProtoTimestampToLocalDate(combinedFilterRequest.getTimeFilter().getDateRange().getEnd());
        List<LegRenderDataProjection> legs = flightScheduleLegRepository
                .findDistinctFlightScheduleLegsByStartAndEndDate(startDate, endDate);

        return applyFilterAndBuild(legs, combinedFilterRequest);
    }

    private SandboxModeResponseObject applyFilterAndBuild(List<LegRenderDataProjection> legs, CombinedFilterRequest combinedFilterRequest) {

        SandboxModeResponseObject.Builder responseBuilder = SandboxModeResponseObject.newBuilder();

        Stream<LegRenderDataProjection> legStream = legs.stream();

        legStream = FilterUtil.applyGeneralFilter(combinedFilterRequest.getGeneralFilter(), combinedFilterRequest.getSelectedAirportFilter(), legStream);
        legStream = FilterUtil.applyTimeFilter(combinedFilterRequest.getTimeFilter(), legStream);

        List<LegRenderDataProjection> preMatureFilterLegs = legStream.collect(Collectors.toCollection(ArrayList::new));
        // Get the furthest and longest leg before applying route filter!
        LegRenderDataProjection furthestLeg = preMatureFilterLegs.stream().max(Comparator.comparing(LegRenderDataProjection::getDistanceKilometers)).orElse(null);
        LegRenderDataProjection longestLeg = preMatureFilterLegs.stream().max(Comparator.comparing(LegRenderDataProjection::getDurationMinutes)).orElse(null);

        legStream = FilterUtil.applyRouteFilter(combinedFilterRequest.getRouteFilter(), preMatureFilterLegs.stream());
        legs = legStream.collect(Collectors.toCollection(ArrayList::new));

        List<LegRender> legRenders = LegRenderBuilder.buildLegRenders(legs);
        List<AirportRender> airportRenders = this.airportService.getAllAirportsWithFilter(combinedFilterRequest.getGeneralFilter(), legRenders);

        responseBuilder.addAllLegRenders(legRenders);
        responseBuilder.addAllAirportRenders(airportRenders);

        if(furthestLeg != null) {
            responseBuilder.setFurthestFlightLeg(LegRenderBuilder.buildLegRender(furthestLeg));
        }

        if(longestLeg != null) {
            responseBuilder.setLongestFlightLeg(LegRenderBuilder.buildLegRender(longestLeg));
        }

        return responseBuilder.build();
    }


}
