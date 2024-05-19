package com.erijl.flightvisualizer.backend.util;

import com.erijl.flightvisualizer.backend.model.entities.Airport;
import com.erijl.flightvisualizer.backend.model.internal.CoordinatePair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MathUtilTest {

    @Test
    public void testCalculateDistanceBetweenAirports() {
        Airport originAirport = new Airport(); // HAM - Hamburg Airport
        originAirport.setLatitude(BigDecimal.valueOf(53.630300));
        originAirport.setLongitude(BigDecimal.valueOf(9.988300));

        Airport destinationAirport = new Airport(); // FRA - Frankfurt Airport
        destinationAirport.setLatitude(BigDecimal.valueOf(50.033100));
        destinationAirport.setLongitude(BigDecimal.valueOf(8.570600));

        int distance = MathUtil.calculateDistanceBetweenAirports(originAirport, destinationAirport);

        assertEquals(411, distance); // correct
    }

    @Test
    public void testCalculateDrawableCoordinatesWithin180thMeridian() {
        Airport originAirport = new Airport(); // HAM - Hamburg Airport
        originAirport.setLatitude(BigDecimal.valueOf(53.630300));
        originAirport.setLongitude(BigDecimal.valueOf(9.988300));

        Airport destinationAirport = new Airport(); // FRA - Frankfurt Airport
        destinationAirport.setLatitude(BigDecimal.valueOf(50.033100));
        destinationAirport.setLongitude(BigDecimal.valueOf(8.570600));

        CoordinatePair coordinatePair = MathUtil.calculateDrawableCoordinates(originAirport, destinationAirport);

        assertEquals(BigDecimal.valueOf(9.988300), coordinatePair.getOriginLongitude());
        assertEquals(BigDecimal.valueOf(53.630300), originAirport.getLatitude());

        assertEquals(BigDecimal.valueOf(8.570600), coordinatePair.getDestinationLongitude());
        assertEquals(BigDecimal.valueOf(50.033100), destinationAirport.getLatitude());
    }

    @Test
    public void testCalculateDrawableCoordinatesAcross180thMeridianRightToLeft() {
        Airport originAirport = new Airport(); // SFO - San Francisco International Airport
        originAirport.setLongitude(BigDecimal.valueOf(-122.391700));
        originAirport.setLatitude(BigDecimal.valueOf(37.618900));

        Airport destinationAirport = new Airport(); // SYD - Sydney Kingsford Smith Airport
        destinationAirport.setLongitude(BigDecimal.valueOf(151.177200));
        destinationAirport.setLatitude(BigDecimal.valueOf(-33.946100));

        CoordinatePair coordinatePair = MathUtil.calculateDrawableCoordinates(originAirport, destinationAirport);

        assertEquals(BigDecimal.valueOf(237.608300), coordinatePair.getOriginLongitude());
        assertEquals(BigDecimal.valueOf(37.618900), originAirport.getLatitude());

        assertEquals(BigDecimal.valueOf(151.177200), coordinatePair.getDestinationLongitude());
        assertEquals(BigDecimal.valueOf(-33.946100), destinationAirport.getLatitude());
    }

    @Test
    public void testCalculateDrawableCoordinatesAcross180thMeridianLeftToRight() {
        Airport originAirport = new Airport(); // SYD - Sydney Kingsford Smith Airport
        originAirport.setLongitude(BigDecimal.valueOf(151.177200));
        originAirport.setLatitude(BigDecimal.valueOf(-33.946100));

        Airport destinationAirport = new Airport(); // SFO - San Francisco International Airport
        destinationAirport.setLongitude(BigDecimal.valueOf(-122.391700));
        destinationAirport.setLatitude(BigDecimal.valueOf(37.618900));
        CoordinatePair coordinatePair = MathUtil.calculateDrawableCoordinates(originAirport, destinationAirport);

        assertEquals(BigDecimal.valueOf(151.177200), coordinatePair.getOriginLongitude());
        assertEquals(BigDecimal.valueOf(-33.946100), originAirport.getLatitude());

        assertEquals(BigDecimal.valueOf(237.608300), coordinatePair.getDestinationLongitude());
        assertEquals(BigDecimal.valueOf(37.618900), destinationAirport.getLatitude());
    }

    @Test
    public void testCalculateDrawableCoordinatesWitError() {
        Airport originAirport = new Airport();

        Airport destinationAirport = new Airport(); // SFO - San Francisco International Airport
        destinationAirport.setLongitude(BigDecimal.valueOf(-122.391700));
        destinationAirport.setLatitude(BigDecimal.valueOf(37.618900));
        CoordinatePair coordinatePair = MathUtil.calculateDrawableCoordinates(originAirport, destinationAirport);

        assertEquals(BigDecimal.valueOf(0), coordinatePair.getOriginLongitude());
        assertEquals(null, originAirport.getLatitude());

        assertEquals(BigDecimal.valueOf(0), coordinatePair.getDestinationLongitude());
        assertEquals(BigDecimal.valueOf(37.618900), destinationAirport.getLatitude());
    }

    @Test
    public void testCalculateDrawableCoordinatesAcross180thMeridianIAHtoAKL() {
        Airport originAirport = new Airport(); // IAH - Houston
        originAirport.setLongitude(BigDecimal.valueOf(-95.341400));
        originAirport.setLatitude(BigDecimal.valueOf(29.984400));

        Airport destinationAirport = new Airport(); // AKL - Aukland
        destinationAirport.setLongitude(BigDecimal.valueOf(174.791700));
        destinationAirport.setLatitude(BigDecimal.valueOf(-37.009700));

        CoordinatePair coordinatePairIAHtoAKL = MathUtil.calculateDrawableCoordinates(originAirport, destinationAirport);
        CoordinatePair coordinatePairAKLtoIAH = MathUtil.calculateDrawableCoordinates(destinationAirport, originAirport);

        assertEquals(BigDecimal.valueOf(264.658600), coordinatePairIAHtoAKL.getOriginLongitude());
        assertEquals(BigDecimal.valueOf(174.791700), coordinatePairIAHtoAKL.getDestinationLongitude());

        assertEquals(BigDecimal.valueOf(174.791700), coordinatePairAKLtoIAH.getOriginLongitude());
        assertEquals(BigDecimal.valueOf(264.658600), coordinatePairAKLtoIAH.getDestinationLongitude());
    }
}