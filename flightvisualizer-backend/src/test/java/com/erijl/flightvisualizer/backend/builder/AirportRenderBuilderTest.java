package com.erijl.flightvisualizer.backend.builder;

import com.erijl.flightvisualizer.backend.model.projections.AirportRenderDataProjection;
import com.erijl.flightvisualizer.protos.objects.AirportRender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AirportRenderBuilderTest {

    @Test
    public void testBuildAirportRender() {
        AirportRenderDataProjection projection = mock(AirportRenderDataProjection.class);
        when(projection.getIataCode()).thenReturn("LHR");
        when(projection.getLongitude()).thenReturn(-0.4543);
        when(projection.getLatitude()).thenReturn(51.4700);

        AirportRender result = AirportRenderBuilder.buildAirportRender(projection);

        assertEquals("LHR", result.getIataCode());
        assertEquals(-0.4543, result.getCoordinate().getLongitude());
        assertEquals(51.4700, result.getCoordinate().getLatitude());
    }

    @Test
    public void testBuildAirportRender_nullProjection() {
        assertThrows(NullPointerException.class, () -> AirportRenderBuilder.buildAirportRender(null),
                "Expected IllegalArgumentException for null projection");
    }

    @Test
    public void testBuildAirportRenderList() {
        List<AirportRenderDataProjection> projections = new ArrayList<>();
        projections.add(createMockProjection("JFK", -73.7781, 40.6413));
        projections.add(createMockProjection("SFO", -122.3750, 37.6189));

        List<AirportRender> results = AirportRenderBuilder.buildAirportRenderList(projections);

        AirportRender firstResult = results.getFirst();
        assertEquals(2, results.size());
        assertEquals("JFK", firstResult.getIataCode());
        assertEquals(-73.7781, firstResult.getCoordinate().getLongitude());
        assertEquals(40.6413, firstResult.getCoordinate().getLatitude());
        assertEquals("SFO", results.get(1).getIataCode());
        assertEquals(-122.3750, results.get(1).getCoordinate().getLongitude());
        assertEquals(37.6189, results.get(1).getCoordinate().getLatitude());
    }

    @Test
    public void testBuildAirportRenderList_nullProjections() {
        assertThrows(NullPointerException.class, () -> AirportRenderBuilder.buildAirportRenderList(null),
                "Expected NullPointerException for null projections");
    }

    private AirportRenderDataProjection createMockProjection(String iataCode, double longitude, double latitude) {
        AirportRenderDataProjection projection = mock(AirportRenderDataProjection.class);
        when(projection.getIataCode()).thenReturn(iataCode);
        when(projection.getLongitude()).thenReturn(longitude);
        when(projection.getLatitude()).thenReturn(latitude);
        return projection;
    }
}
