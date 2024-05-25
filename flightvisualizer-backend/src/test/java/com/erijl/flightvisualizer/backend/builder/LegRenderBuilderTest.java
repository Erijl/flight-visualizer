package com.erijl.flightvisualizer.backend.builder;

import com.erijl.flightvisualizer.backend.model.projections.LegRenderDataProjection;
import com.erijl.flightvisualizer.protos.objects.LegRender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LegRenderBuilderTest {

    @Test
    public void testBuildLegRender() {
        LegRenderDataProjection projection = mock(LegRenderDataProjection.class);
        when(projection.getOriginAirportIataCode()).thenReturn("JFK");
        when(projection.getDestinationAirportIataCode()).thenReturn("LHR");
        when(projection.getDurationMinutes()).thenReturn(420);
        when(projection.getDistanceKilometers()).thenReturn(5500);
        when(projection.getOriginLatitude()).thenReturn(40.6413);
        when(projection.getOriginLongitude()).thenReturn(-73.7781);
        when(projection.getDestinationLatitude()).thenReturn(51.4700);
        when(projection.getDestinationLongitude()).thenReturn(-0.4543);
        when(projection.getOriginAirportName()).thenReturn("John F. Kennedy International Airport");
        when(projection.getDestinationAirportName()).thenReturn("London Heathrow Airport");
        when(projection.getAircraftDepartureTimeUtc()).thenReturn(1672531200);
        when(projection.getAircraftArrivalTimeUtc()).thenReturn(1672552800);

        LegRender result = LegRenderBuilder.buildLegRender(projection);

        assertEquals("JFK", result.getOriginAirportIataCode());
        assertEquals("LHR", result.getDestinationAirportIataCode());
        assertEquals(420, result.getDurationMinutes());
        assertEquals(5500, result.getDistanceKilometers());
        assertEquals(2, result.getCoordinatesCount());
        assertEquals(40.6413, result.getCoordinates(0).getLatitude());
        assertEquals(-73.7781, result.getCoordinates(0).getLongitude());
        assertEquals(51.4700, result.getCoordinates(1).getLatitude());
        assertEquals(-0.4543, result.getCoordinates(1).getLongitude());
        assertEquals("John F. Kennedy International Airport", result.getDetails().getOriginAirportName());
        assertEquals("London Heathrow Airport", result.getDetails().getDestinationAirportName());
        assertEquals(1672531200, result.getDetails().getDepartureTimeUtc());
        assertEquals(1672552800, result.getDetails().getArrivalTimeUtc());
    }

    @Test
    public void testBuildLegRender_nullProjection() {
        assertThrows(NullPointerException.class, () -> LegRenderBuilder.buildLegRender(null),
                "Expected NullPointerException for null projection");
    }

    @Test
    public void testBuildLegRenderList_nullProjections() {
        assertThrows(NullPointerException.class, () -> LegRenderBuilder.buildLegRenderList(null),
                "Expected NullPointerException for null projections list");
    }
}
