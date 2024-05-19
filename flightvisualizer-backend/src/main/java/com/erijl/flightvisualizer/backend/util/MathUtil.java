package com.erijl.flightvisualizer.backend.util;

import com.erijl.flightvisualizer.backend.model.entities.Airport;
import com.erijl.flightvisualizer.backend.model.internal.CoordinatePair;

import java.math.BigDecimal;

public class MathUtil {

    private final static double EARTH_RADIUS_IN_METRES = 6371e3;

    private MathUtil() {
    }

    public static int calculateDistanceBetweenAirports(Airport originAirport, Airport destinationAirport) {

        if ((originAirport == null || destinationAirport == null)
                || (originAirport.getLatitude() == null || originAirport.getLongitude() == null)
                || (destinationAirport.getLatitude() == null || destinationAirport.getLongitude() == null)) {
            return 0;
        }

        double originLatitudeInRadians = Math.toRadians(originAirport.getLatitude().doubleValue());
        double destinationLatitudeInRadians = Math.toRadians(destinationAirport.getLatitude().doubleValue());
        double deltaLatitudeInRadians = Math.toRadians(destinationAirport.getLatitude().doubleValue() - originAirport.getLatitude().doubleValue());
        double deltaLongitudeInRadians = Math.toRadians(destinationAirport.getLongitude().doubleValue() - originAirport.getLongitude().doubleValue());

        double haversineFormulaPartA = Math.sin(deltaLatitudeInRadians / 2) * Math.sin(deltaLatitudeInRadians / 2) +
                Math.cos(originLatitudeInRadians) * Math.cos(destinationLatitudeInRadians) *
                        Math.sin(deltaLongitudeInRadians / 2) * Math.sin(deltaLongitudeInRadians / 2);
        double haversineFormulaPartC = 2 * Math.atan2(Math.sqrt(haversineFormulaPartA), Math.sqrt(1 - haversineFormulaPartA));

        return ((int) Math.floor(EARTH_RADIUS_IN_METRES * haversineFormulaPartC)) / 1000;
    }

    public static CoordinatePair calculateDrawableCoordinates(Airport originAirport, Airport destinationAirport) {
        CoordinatePair coordinatePair = new CoordinatePair();
        coordinatePair.setOriginLongitude(originAirport.getLongitude());
        coordinatePair.setDestinationLongitude(destinationAirport.getLongitude());
        if (coordinatePair.getOriginLongitude() == null || coordinatePair.getDestinationLongitude() == null) {
            return new CoordinatePair(BigDecimal.ZERO, BigDecimal.ZERO);
        }

        BigDecimal diffLongitude = coordinatePair.getDestinationLongitude().subtract(coordinatePair.getOriginLongitude());
        if (diffLongitude.abs().compareTo(BigDecimal.valueOf(180)) > 0) {
            if (diffLongitude.compareTo(BigDecimal.ZERO) > 0) {
                coordinatePair.setOriginLongitude(coordinatePair.getOriginLongitude().add(BigDecimal.valueOf(360)));
            } else {
                coordinatePair.setDestinationLongitude(coordinatePair.getDestinationLongitude().add(BigDecimal.valueOf(360)));
            }
        }

        return coordinatePair;
    }
}
