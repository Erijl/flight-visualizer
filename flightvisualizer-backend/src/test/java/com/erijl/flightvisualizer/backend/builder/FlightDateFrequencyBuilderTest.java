package com.erijl.flightvisualizer.backend.builder;

import com.erijl.flightvisualizer.backend.model.projections.FlightDateFrequencyProjection;
import com.erijl.flightvisualizer.protos.objects.FlightDateFrequency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FlightDateFrequencyBuilderTest {

    @Test
    public void testBuildFlightDateFrequency() {
        FlightDateFrequencyProjection projection = mock(FlightDateFrequencyProjection.class);
        LocalDate date = LocalDate.of(2023, 12, 25);
        when(projection.getStartDateUtc()).thenReturn("2023-12-25");
        when(projection.getFlightCount()).thenReturn(5);

        FlightDateFrequency result = FlightDateFrequencyBuilder.buildFlightDateFrequency(projection);

        assertEquals(date.atStartOfDay(ZoneId.of("UTC")).toEpochSecond(), result.getDate().getSeconds());
        assertEquals(5, result.getFrequency());
    }

    @Test
    public void testBuildFlightDateFrequency_nullProjection() {
        assertThrows(NullPointerException.class, () -> FlightDateFrequencyBuilder.buildFlightDateFrequency(null),
                "Expected NullPointerException for null projection");
    }

    @Test
    public void testBuildFlightDateFrequencyList() {
        List<FlightDateFrequencyProjection> projections = new ArrayList<>();
        projections.add(createMockProjection("2023-12-25", 5));
        projections.add(createMockProjection("2024-01-01", 10));

        List<FlightDateFrequency> results = FlightDateFrequencyBuilder.buildFLightDateFrequencyList(projections);

        assertEquals(2, results.size());

        FlightDateFrequency firstResult = results.getFirst();
        assertEquals(LocalDate.of(2023, 12, 25).atStartOfDay(ZoneId.of("UTC")).toEpochSecond(), firstResult.getDate().getSeconds());
        assertEquals(5, firstResult.getFrequency());

        FlightDateFrequency secondResult = results.get(1);
        assertEquals(LocalDate.of(2024, 1, 1).atStartOfDay(ZoneId.of("UTC")).toEpochSecond(), secondResult.getDate().getSeconds());
        assertEquals(10, secondResult.getFrequency());
    }

    @Test
    public void testBuildFlightDateFrequencyList_nullProjections() {
        assertThrows(NullPointerException.class, () -> FlightDateFrequencyBuilder.buildFLightDateFrequencyList(Collections.singletonList(null)),
                "Expected NullPointerException for null projections list");
    }

    private FlightDateFrequencyProjection createMockProjection(String date, int frequency) {
        FlightDateFrequencyProjection projection = mock(FlightDateFrequencyProjection.class);
        when(projection.getStartDateUtc()).thenReturn(date);
        when(projection.getFlightCount()).thenReturn(frequency);
        return projection;
    }
}
