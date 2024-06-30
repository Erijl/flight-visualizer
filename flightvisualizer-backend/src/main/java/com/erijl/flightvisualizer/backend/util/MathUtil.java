package com.erijl.flightvisualizer.backend.util;

import com.erijl.flightvisualizer.backend.model.entities.Airport;
import com.erijl.flightvisualizer.backend.model.internal.CoordinatePair;

import java.math.BigDecimal;
import java.math.MathContext;

public class MathUtil {

    private final static double EARTH_RADIUS_IN_METRES = 6371e3;

    private MathUtil() {
    }

    /**
     * Calculates the distance between two airports in kilometers using the Haversine formula
     *
     * @param originAirport      the origin {@link Airport}
     * @param destinationAirport the destination {@link Airport}
     * @return the distance between the two {@link Airport}s in kilometers
     */
    public static int calculateDistanceBetweenAirports(Airport originAirport, Airport destinationAirport) {
        if ((originAirport == null || destinationAirport == null)
                || (originAirport.getLatitude() == null || originAirport.getLongitude() == null)
                || (destinationAirport.getLatitude() == null || destinationAirport.getLongitude() == null)) {
            return 0;
        }

        double originLatitudeInRadians = Math.toRadians(originAirport.getLatitude().doubleValue());
        double haversineFormulaPartA = getHaversineFormulaPartA(originAirport, destinationAirport, originLatitudeInRadians);
        double haversineFormulaPartC = 2 * Math.atan2(Math.sqrt(haversineFormulaPartA), Math.sqrt(1 - haversineFormulaPartA));

        return ((int) Math.floor(EARTH_RADIUS_IN_METRES * haversineFormulaPartC)) / 1000;
    }

    public static BigDecimal[] calculateIntermediateCoordinates(Airport origin, Airport destination, BigDecimal percentageTraveled) {
        if (origin == null || destination == null || percentageTraveled == null
                || percentageTraveled.compareTo(BigDecimal.ZERO) < 0 || percentageTraveled.compareTo(BigDecimal.ONE) > 1) {
            return null;
        }

        int totalDistanceInMeters = calculateDistanceBetweenAirports(origin, destination) * 1000;
        BigDecimal totalDistance = BigDecimal.valueOf(totalDistanceInMeters);

        BigDecimal distanceTraveled = totalDistance.multiply(percentageTraveled);

        double originLatitudeRadians = Math.toRadians(origin.getLatitude().doubleValue());
        double originLongitudeRadians = Math.toRadians(origin.getLongitude().doubleValue());
        double destinationLatitudeRadians = Math.toRadians(destination.getLatitude().doubleValue());
        double destinationLongitudeRadians = Math.toRadians(destination.getLongitude().doubleValue());

        double y = Math.sin(destinationLongitudeRadians - originLongitudeRadians) * Math.cos(destinationLatitudeRadians);
        double x = Math.cos(originLatitudeRadians) * Math.sin(destinationLatitudeRadians) -
                Math.sin(originLatitudeRadians) * Math.cos(destinationLatitudeRadians) * Math.cos(destinationLongitudeRadians - originLongitudeRadians);
        double bearing = Math.atan2(y, x);

        double angularDistance = distanceTraveled.divide(BigDecimal.valueOf(EARTH_RADIUS_IN_METRES), MathContext.DECIMAL128).doubleValue();

        double intermediateLatitudeRadians = Math.asin(Math.sin(originLatitudeRadians) * Math.cos(angularDistance) +
                Math.cos(originLatitudeRadians) * Math.sin(angularDistance) * Math.cos(bearing));

        double intermediateLongitudeRadians = originLongitudeRadians + Math.atan2(Math.sin(bearing) * Math.sin(angularDistance) * Math.cos(originLatitudeRadians),
                Math.cos(angularDistance) - Math.sin(originLatitudeRadians) * Math.sin(intermediateLatitudeRadians));

        return new BigDecimal[] {
                BigDecimal.valueOf(Math.toDegrees(intermediateLatitudeRadians)),
                BigDecimal.valueOf(Math.toDegrees(intermediateLongitudeRadians))
        };
    }

    /**
     * Calculates at which coordinates to draw a line between two airports
     * due to the 180° meridian longitude border (anti-meridian)
     *
     * @param originAirport      the origin {@link Airport}
     * @param destinationAirport the destination {@link Airport}
     * @return a {@link CoordinatePair} containing the respective origin and destination longitudes
     */
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

    private static double getHaversineFormulaPartA(Airport originAirport, Airport destinationAirport, double originLatitudeInRadians) {
        double destinationLatitudeInRadians = Math.toRadians(destinationAirport.getLatitude().doubleValue());
        double deltaLatitudeInRadians = Math.toRadians(destinationAirport.getLatitude().doubleValue() - originAirport.getLatitude().doubleValue());
        double deltaLongitudeInRadians = Math.toRadians(destinationAirport.getLongitude().doubleValue() - originAirport.getLongitude().doubleValue());

        return Math.sin(deltaLatitudeInRadians / 2) * Math.sin(deltaLatitudeInRadians / 2) +
                Math.cos(originLatitudeInRadians) * Math.cos(destinationLatitudeInRadians) *
                        Math.sin(deltaLongitudeInRadians / 2) * Math.sin(deltaLongitudeInRadians / 2);
    }
}
