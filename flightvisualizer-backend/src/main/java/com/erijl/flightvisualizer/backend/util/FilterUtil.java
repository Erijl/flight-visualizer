package com.erijl.flightvisualizer.backend.util;

import com.erijl.flightvisualizer.backend.model.projections.LegRenderDataProjection;
import com.erijl.flightvisualizer.protos.enums.AircraftTimeFilterType;
import com.erijl.flightvisualizer.protos.enums.RouteDisplayType;
import com.erijl.flightvisualizer.protos.enums.RouteFilterType;
import com.erijl.flightvisualizer.protos.filter.GeneralFilter;
import com.erijl.flightvisualizer.protos.filter.RouteFilter;
import com.erijl.flightvisualizer.protos.filter.SelectedAirportFilter;
import com.erijl.flightvisualizer.protos.filter.TimeFilter;
import com.erijl.flightvisualizer.protos.objects.TimeRange;

import java.util.function.Function;
import java.util.stream.Stream;

public class FilterUtil {

    private static final int TIMEZONE_REGION_INDEX = 0;
    private static final String TIMEZONE_SEPARATOR = "/";
    private static final int MIN_MINUTES_IN_DAY = 0;
    private static final int MAX_MINUTES_IN_DAY = 1439;

    private FilterUtil() {
    }

    /**
     * Applies the {@link GeneralFilter} to a {@link LegRenderDataProjection} {@link Stream}
     *
     * @param generalFilter         {@link GeneralFilter} to apply
     * @param selectedAirportFilter {@link SelectedAirportFilter} to apply when the {@link RouteDisplayType} is {@link RouteDisplayType#ROUTEDISPLAYTYPE_SPECIFICAIRPORT}
     * @param legStream             {@link LegRenderDataProjection} {@link Stream} to apply the filters to
     * @return filtered {@link Stream} of {@link LegRenderDataProjection}
     */
    public static Stream<LegRenderDataProjection> applyGeneralFilter(GeneralFilter generalFilter, SelectedAirportFilter selectedAirportFilter, Stream<LegRenderDataProjection> legStream) {
        switch (generalFilter.getRouteDisplayType()) {
            default:
            case RouteDisplayType.ROUTEDISPLAYTYPE_ALL:
                legStream = legStream;
                break;
            case RouteDisplayType.ROUTEDISPLAYTYPE_SPECIFICAIRPORT:
                String iataCode = selectedAirportFilter.getIataCode();
                legStream = legStream.filter(leg -> (leg.getDestinationAirportIataCode().equals(iataCode) && selectedAirportFilter.getIncludingArrivals()) || (leg.getDestinationAirportIataCode().equals(iataCode) && selectedAirportFilter.getIncludingDepartures()));
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

        return legStream;
    }

    /**
     * Applies the {@link TimeFilter} to a {@link LegRenderDataProjection} {@link Stream}
     *
     * @param timeFilter {@link TimeFilter} to apply
     * @param legStream  {@link LegRenderDataProjection} {@link Stream} to apply the filters to
     * @return filtered {@link Stream} of {@link LegRenderDataProjection}
     */
    public static Stream<LegRenderDataProjection> applyTimeFilter(TimeFilter timeFilter, Stream<LegRenderDataProjection> legStream) {
        legStream = legStream.filter(leg -> {
            boolean isDifferentDayDeparture = (timeFilter.getIncludeDifferentDayDepartures() && leg.getAircraftDepartureTimeDateDiffUtc() >= 0);
            boolean isSameDayDeparture = (!timeFilter.getIncludeDifferentDayDepartures() && leg.getAircraftDepartureTimeDateDiffUtc() == 0);
            boolean isDifferentDayArrival = (timeFilter.getIncludeDifferentDayArrivals() && leg.getAircraftArrivalTimeDateDiffUtc() >= 0);
            boolean isSameDayArrival = (!timeFilter.getIncludeDifferentDayArrivals() && leg.getAircraftArrivalTimeDateDiffUtc() == 0);

            if ((isDifferentDayDeparture || isSameDayDeparture) && (isDifferentDayArrival || isSameDayArrival)) {
                return switch (timeFilter.getAircraftDepOrArrInTimeRange()) {
                    default -> isLegInTimeRange(leg, timeFilter);
                    case AircraftTimeFilterType.ARRIVALANDDEPARTURE -> isLegInTimeRange(leg, timeFilter); // keep this line for readability
                    case AircraftTimeFilterType.ARRIVAL -> isArrivalInTimeRange(leg, timeFilter);
                    case AircraftTimeFilterType.DEPARTURE -> isDepartureInTimeRange(leg, timeFilter);
                };
            }

            return false;
        });

        return legStream;
    }

    /**
     * Applies the {@link RouteFilter} to a {@link LegRenderDataProjection} {@link Stream}
     *
     * @param routeFilter {@link RouteFilter} to apply
     * @param legStream   {@link LegRenderDataProjection} {@link Stream} to apply the filters to
     * @return filtered {@link Stream} of {@link LegRenderDataProjection}
     */
    public static Stream<LegRenderDataProjection> applyRouteFilter(RouteFilter routeFilter, Stream<LegRenderDataProjection> legStream) {
        switch (routeFilter.getRouteFilterType()) { //TODO absolutly broken smh
            case RouteFilterType.DISTANCE:
                legStream = legStream.filter(leg -> isLegIntRouteFilterRange(leg, routeFilter, LegRenderDataProjection::getDistanceKilometers));
            case RouteFilterType.DURATION:
                legStream = legStream.filter(leg -> isLegIntRouteFilterRange(leg, routeFilter, LegRenderDataProjection::getDurationMinutes));
        }

        return legStream;
    }

    private static boolean isLegIntRouteFilterRange(LegRenderDataProjection leg, RouteFilter routeFilter, Function<LegRenderDataProjection, Integer> filterValueExtractor) {
        return routeFilter.getStart() <= filterValueExtractor.apply(leg) && filterValueExtractor.apply(leg) <= routeFilter.getEnd();
    }

    private static boolean isLegInTimeRange(LegRenderDataProjection leg, TimeFilter timeFilter) {
        return isArrivalInTimeRange(leg, timeFilter) && isDepartureInTimeRange(leg, timeFilter);
    }

    private static boolean isArrivalInTimeRange(LegRenderDataProjection leg, TimeFilter timeFilter) {
        return isInTimeRange(leg, timeFilter.getTimeRange(), LegRenderDataProjection::getAircraftArrivalTimeUtc);
    }

    private static boolean isDepartureInTimeRange(LegRenderDataProjection leg, TimeFilter timeFilter) {
        return isInTimeRange(leg, timeFilter.getTimeRange(), LegRenderDataProjection::getAircraftDepartureTimeUtc);
    }

    private static boolean isInTimeRange(LegRenderDataProjection leg, TimeRange timeRange, Function<LegRenderDataProjection, Integer> timeExtractor) {
        if(timeRange.getInverted()) {
            return MIN_MINUTES_IN_DAY <= timeExtractor.apply(leg) && timeExtractor.apply(leg) <= timeRange.getStart() &&
                    timeRange.getEnd() <= timeExtractor.apply(leg) && timeExtractor.apply(leg) <= MAX_MINUTES_IN_DAY;
        } else {
            return timeRange.getStart() <= timeExtractor.apply(leg) && timeExtractor.apply(leg) <= timeRange.getEnd();
        }
    }
}
