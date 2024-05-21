package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.builder.DetailedLegInformationBuilder;
import com.erijl.flightvisualizer.backend.builder.LegRenderBuilder;
import com.erijl.flightvisualizer.backend.model.projections.LegRenderDataProjection;
import com.erijl.flightvisualizer.backend.model.repository.FlightScheduleLegRepository;
import com.erijl.flightvisualizer.backend.util.TimeUtil;
import com.erijl.flightvisualizer.backend.util.FilterUtil;
import com.erijl.flightvisualizer.backend.validators.*;
import com.erijl.flightvisualizer.protos.dtos.SandboxModeResponseObject;
import com.erijl.flightvisualizer.protos.filter.CombinedFilterRequest;
import com.erijl.flightvisualizer.protos.filter.SpecificRouteFilterRequest;
import com.erijl.flightvisualizer.protos.objects.AirportRender;
import com.erijl.flightvisualizer.protos.objects.DetailedLegInformations;
import com.erijl.flightvisualizer.protos.objects.LegRender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    /**
     * Get detailed leg information for a specific route
     *
     * @param request a valid {@link SpecificRouteFilterRequest}
     * @return a {@link DetailedLegInformations}
     */
    public DetailedLegInformations getLegsForRouteDetailed(SpecificRouteFilterRequest request) {
        SpecificRouteFilterRequestValidator.validate(request);

        LocalDate startDate = TimeUtil.convertProtoTimestampToLocalDate(request.getTimeFilter().getDateRange().getStart());
        LocalDate endDate = TimeUtil.convertProtoTimestampToLocalDate(request.getTimeFilter().getDateRange().getEnd());
        LegRender legRender = request.getLegRender();
        List<LegRenderDataProjection> legs = flightScheduleLegRepository
                .getDetailedLegInformationForRoute(startDate, endDate, legRender.getOriginAirportIataCode(), legRender.getDestinationAirportIataCode());

        Stream<LegRenderDataProjection> legStream = legs.stream();

        legStream = FilterUtil.applyTimeFilter(request.getTimeFilter(), legStream);
        legs = legStream.collect(Collectors.toCollection(ArrayList::new));

        return DetailedLegInformations.newBuilder()
                .addAllDetailedLegs(DetailedLegInformationBuilder.buildDetailedLegInformationList(legs))
                .build();
    }

    /**
     * Get distinct flight schedule legs for rendering
     *
     * @param combinedFilterRequest a valid {@link CombinedFilterRequest}
     * @return a {@link SandboxModeResponseObject} containing the filtered {@link LegRender}s and {@link AirportRender}s
     */
    public SandboxModeResponseObject getDistinctFlightScheduleLegsForRendering(CombinedFilterRequest combinedFilterRequest) {
        CombinedFilterRequestValidator.validate(combinedFilterRequest);

        //TODO possible error when selecting too many days (check db performance)
        LocalDate startDate = TimeUtil.convertProtoTimestampToLocalDate(combinedFilterRequest.getTimeFilter().getDateRange().getStart());
        LocalDate endDate = TimeUtil.convertProtoTimestampToLocalDate(combinedFilterRequest.getTimeFilter().getDateRange().getEnd());
        List<LegRenderDataProjection> legs = flightScheduleLegRepository
                .findDistinctFlightScheduleLegsByStartAndEndDate(startDate, endDate);

        return applyFilterAndBuild(legs, combinedFilterRequest);
    }

    private SandboxModeResponseObject applyFilterAndBuild(List<LegRenderDataProjection> legs, CombinedFilterRequest combinedFilterRequest) {

        SandboxModeResponseObject.Builder responseBuilder = SandboxModeResponseObject.newBuilder();

        LegRenderDataProjection furthestLeg = legs.stream().max(Comparator.comparing(LegRenderDataProjection::getDistanceKilometers)).orElse(null);
        LegRenderDataProjection longestLeg = legs.stream().max(Comparator.comparing(LegRenderDataProjection::getDurationMinutes)).orElse(null);

        Stream<LegRenderDataProjection> legStream = legs.stream();

        legStream = FilterUtil.applyGeneralFilter(combinedFilterRequest.getGeneralFilter(), combinedFilterRequest.getSelectedAirportFilter(), legStream);
        legStream = FilterUtil.applyTimeFilter(combinedFilterRequest.getTimeFilter(), legStream);
        legStream = FilterUtil.applyRouteFilter(combinedFilterRequest.getRouteFilter(), legStream);
        legs = legStream.collect(Collectors.toCollection(ArrayList::new));

        List<LegRender> legRenders = LegRenderBuilder.buildLegRenderList(legs);
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
