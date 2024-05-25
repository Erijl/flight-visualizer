package com.erijl.flightvisualizer.backend.builder;

import com.erijl.flightvisualizer.backend.model.entities.Airport;
import com.erijl.flightvisualizer.protos.objects.AirportDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AirportDetailsBuilderTest {

    @Test
    public void testBuildAirportDetails() {
        Airport airport = mock(Airport.class);
        when(airport.getIataAirportCode()).thenReturn("JFK");
        when(airport.getAirportName()).thenReturn("John F. Kennedy International Airport");
        when(airport.getLongitude()).thenReturn(BigDecimal.valueOf(-73.7781));
        when(airport.getLatitude()).thenReturn(BigDecimal.valueOf(40.6413));
        when(airport.getIataCityCode()).thenReturn("NYC");
        when(airport.getIsoCountryCode()).thenReturn("US");
        when(airport.getOffsetUtc()).thenReturn("-05:00");
        when(airport.getTimezoneId()).thenReturn("America/New_York");

        AirportDetails result = AirportDetailsBuilder.buildAirportDetails(airport);

        assertEquals("JFK", result.getIataCode());
        assertEquals("John F. Kennedy International Airport", result.getName());
        assertEquals(-73.7781, result.getCoordinate().getLongitude(), 0.0001);
        assertEquals(40.6413, result.getCoordinate().getLatitude(), 0.0001);
        assertEquals("NYC", result.getIataCityCode());
        assertEquals("US", result.getIsoCountryCode());
        assertEquals("-05:00", result.getOffsetUtc());
        assertEquals("America/New_York", result.getTimezoneId());
    }

    @Test
    public void testBuildAirportDetailsWithNullAirport() {
        assertThrows(NullPointerException.class, () -> AirportDetailsBuilder.buildAirportDetails(null),
                "Expected IllegalArgumentException for null Airport input");
    }
}
