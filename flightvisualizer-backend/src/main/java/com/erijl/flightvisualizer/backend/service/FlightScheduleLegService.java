package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegWithDistance;
import com.erijl.flightvisualizer.backend.model.enums.LocationType;
import com.erijl.flightvisualizer.backend.model.projections.LegRenderDataProjection;
import com.erijl.flightvisualizer.backend.model.repository.FlightScheduleLegRepository;
import com.erijl.flightvisualizer.backend.util.CustomTimeUtil;
import com.erijl.flightvisualizer.backend.util.MathUtil;
import com.erijl.flightvisualizer.backend.validators.GeneralFilterValidator;
import com.erijl.flightvisualizer.backend.validators.RouteFilterValidator;
import com.erijl.flightvisualizer.backend.validators.SelectedAirportFilterValidator;
import com.erijl.flightvisualizer.backend.validators.TimeFilterValidator;
import com.erijl.flightvisualizer.protos.enums.AircraftTimeFilterType;
import com.erijl.flightvisualizer.protos.enums.RouteDisplayType;
import com.erijl.flightvisualizer.protos.enums.RouteFilterType;
import com.erijl.flightvisualizer.protos.filter.CombinedFilterRequest;
import com.erijl.flightvisualizer.protos.filter.RouteFilter;
import com.erijl.flightvisualizer.protos.filter.TimeFilter;
import com.erijl.flightvisualizer.protos.objects.Coordinate;
import com.erijl.flightvisualizer.protos.objects.LegRender;
import com.erijl.flightvisualizer.protos.objects.TimeRange;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FlightScheduleLegService {

    private static final int TIMEZONE_REGION_INDEX = 0;
    private static final String TIMEZONE_SEPARATOR = "/";
    private static final int MIN_MINUTES_IN_DAY = 0;
    private static final int MAX_MINUTES_IN_DAY = 1439;

    private final FlightScheduleLegRepository flightScheduleLegRepository;

    public FlightScheduleLegService(FlightScheduleLegRepository flightScheduleLegRepository) {
        this.flightScheduleLegRepository = flightScheduleLegRepository;
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

    public List<LegRender> getDistinctFlightScheduleLegsForRendering(CombinedFilterRequest combinedFilterRequest) {
        TimeFilterValidator.validate(combinedFilterRequest.getTimeFilter());
        RouteFilterValidator.validate(combinedFilterRequest.getRouteFilter());
        GeneralFilterValidator.validate(combinedFilterRequest.getGeneralFilter());
        SelectedAirportFilterValidator.validate(combinedFilterRequest.getSelectedAirportFilter());

        //TODO possible error when selecting too many days (check db performance)
        LocalDate startDate = CustomTimeUtil.convertProtoTimestampToLocalDate(combinedFilterRequest.getTimeFilter().getDateRange().getStart());
        LocalDate endDate = CustomTimeUtil.convertProtoTimestampToLocalDate(combinedFilterRequest.getTimeFilter().getDateRange().getEnd());
        List<LegRenderDataProjection> legs = flightScheduleLegRepository
                .findDistinctFlightScheduleLegsByStartAndEndDate(startDate, endDate);

        legs = applyFilter(legs, combinedFilterRequest);

        List<LegRender> legRenders = new ArrayList<>();
        for (LegRenderDataProjection legData : legs) {
            var render = LegRender.newBuilder()
                    .setOriginAirportIataCode(legData.getOriginAirportIataCode())
                    .setDestinationAirportIataCode(legData.getDestinationAirportIataCode())
                    .setDurationMinutes(legData.getDurationMinutes())
                    .setDistanceKilometers(legData.getDistanceKilometers())
                    .addCoordinates(0, Coordinate.newBuilder().setLatitude(legData.getOriginLatitude()).setLongitude(legData.getOriginLongitude()))
                    .addCoordinates(1, Coordinate.newBuilder().setLatitude(legData.getDestinationLatitude()).setLongitude(legData.getDestinationLongitude())).build();
            legRenders.add(render);
        }
        return legRenders;
    }

    private List<LegRenderDataProjection> applyFilter(List<LegRenderDataProjection> legs, CombinedFilterRequest combinedFilterRequest) {
        Stream<LegRenderDataProjection> legStream = legs.stream();

        // apply GeneralFilter
        switch(combinedFilterRequest.getGeneralFilter().getRouteDisplayType()) {
            default:
            case RouteDisplayType.ROUTEDISPLAYTYPE_ALL:
                legStream = legStream;
                break;
            case RouteDisplayType.ROUTEDISPLAYTYPE_SPECIFICAIRPORT:
                String iataCode = combinedFilterRequest.getSelectedAirportFilter().getIataCode();
                legStream = legStream.filter(leg -> leg.getDestinationAirportIataCode().equals(iataCode) || leg.getDestinationAirportIataCode().equals(iataCode));
                break;
            case RouteDisplayType.ROUTEDISPLAYTYPE_ONLYWITHINSAMECOUNTRY:
                legStream = legStream.filter(leg -> leg.getOriginIsoCountryCode().equals(leg.getDestinationIsoCountryCode()));
                break;
            case RouteDisplayType.ROUTEDISPLAYTYPE_WITHINSAMEREGION:
                legStream = legStream.filter(leg -> leg.getOriginTimezoneId().split(TIMEZONE_SEPARATOR)[TIMEZONE_REGION_INDEX].equals(leg.getDestinationTimezoneId().split(TIMEZONE_SEPARATOR)[TIMEZONE_REGION_INDEX]));
                break;
            case RouteDisplayType.ROUTEDISPLAYTYPE_WITHINSAMETIMEZONE:
                legStream = legStream.filter(leg -> leg.getOriginOffsetUtc().equals(leg.getDestinationOffsetUtc()));
                break;
        }

        // apply TimeFilter
        TimeFilter timeFilter = combinedFilterRequest.getTimeFilter();
        legStream = legStream.filter(leg -> {
            boolean isDifferentDayDeparture = (timeFilter.getIncludeDifferentDayDepartures() && leg.getAircraftDepartureTimeDateDiffUtc() >= 0);
            boolean isSameDayDeparture = (!timeFilter.getIncludeDifferentDayDepartures() && leg.getAircraftDepartureTimeDateDiffUtc() == 0);
            boolean isDifferentDayArrival = (timeFilter.getIncludeDifferentDayArrivals() && leg.getAircraftArrivalTimeDateDiffUtc() >= 0);
            boolean isSameDayArrival = (!timeFilter.getIncludeDifferentDayArrivals() && leg.getAircraftArrivalTimeDateDiffUtc() == 0);

            if ((isDifferentDayDeparture || isSameDayDeparture) && (isDifferentDayArrival || isSameDayArrival)) {
                return switch (timeFilter.getAircraftDepOrArrInTimeRange()) {
                    default -> isLegInTimeRange(leg, timeFilter);
                    case AircraftTimeFilterType.ARRIVALANDDEPARTURE -> isLegInTimeRange(leg, timeFilter);
                    case AircraftTimeFilterType.ARRIVAL -> isArrivalInTimeRange(leg, timeFilter);
                    case AircraftTimeFilterType.DEPARTURE -> isDepartureInTimeRange(leg, timeFilter);
                };
            }

            return false;
        });

        // apply RouteFilter
        RouteFilter routeFilter = combinedFilterRequest.getRouteFilter();
        switch (routeFilter.getRouteFilterType()) {
            case RouteFilterType.DISTANCE:
                legStream = legStream.filter(leg -> leg.getDistanceKilometers() >= routeFilter.getStart() && leg.getDistanceKilometers() <= routeFilter.getEnd());
            case RouteFilterType.DURATION:
                legStream = legStream.filter(leg -> leg.getDurationMinutes() >= routeFilter.getStart() && leg.getDurationMinutes() <= routeFilter.getEnd());
        }



        return legStream.collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean isLegInTimeRange(LegRenderDataProjection leg, TimeFilter timeFilter) {
        TimeRange timeRange = timeFilter.getTimeRange();

        if(timeRange.getInverted()) {
            return (MIN_MINUTES_IN_DAY <= leg.getAircraftArrivalTimeUtc() && leg.getAircraftArrivalTimeUtc() <= timeRange.getStart() &&
                    timeRange.getEnd() <= leg.getAircraftArrivalTimeUtc() && leg.getAircraftArrivalTimeUtc() <= MAX_MINUTES_IN_DAY) &&
                    (MIN_MINUTES_IN_DAY <= leg.getAircraftDepartureTimeUtc() && leg.getAircraftDepartureTimeUtc() <= timeRange.getStart() &&
                            timeRange.getEnd() <= leg.getAircraftDepartureTimeUtc() && leg.getAircraftDepartureTimeUtc() <= MAX_MINUTES_IN_DAY);
        } else {
            return timeRange.getStart() <= leg.getAircraftArrivalTimeUtc() && leg.getAircraftArrivalTimeUtc() <= timeRange.getEnd() &&
                    timeRange.getStart() <= leg.getAircraftDepartureTimeUtc() && leg.getAircraftDepartureTimeUtc() <= timeRange.getEnd();
        }
    }

    private boolean isArrivalInTimeRange(LegRenderDataProjection leg, TimeFilter timeFilter) {
        TimeRange timeRange = timeFilter.getTimeRange();

        if(timeRange.getInverted()) {
            return MIN_MINUTES_IN_DAY <= leg.getAircraftArrivalTimeUtc() && leg.getAircraftArrivalTimeUtc() <= timeRange.getStart() &&
                    timeRange.getEnd() <= leg.getAircraftDepartureTimeUtc() && leg.getAircraftDepartureTimeUtc() <= MAX_MINUTES_IN_DAY;
        } else {
            return timeRange.getStart() <= leg.getAircraftArrivalTimeUtc() && leg.getAircraftArrivalTimeUtc() <= timeRange.getEnd();
        }
    }

    private boolean isDepartureInTimeRange(LegRenderDataProjection leg, TimeFilter timeFilter) {
        TimeRange timeRange = timeFilter.getTimeRange();

        if(timeRange.getInverted()) {
            return MIN_MINUTES_IN_DAY <= leg.getAircraftArrivalTimeUtc() && leg.getAircraftArrivalTimeUtc() <= timeRange.getStart() &&
                    timeRange.getEnd() <= leg.getAircraftDepartureTimeUtc() && leg.getAircraftDepartureTimeUtc() <= MAX_MINUTES_IN_DAY;
        } else {
            return timeRange.getStart() <= leg.getAircraftDepartureTimeUtc() && leg.getAircraftDepartureTimeUtc() <= timeRange.getEnd();
        }
    }
}
