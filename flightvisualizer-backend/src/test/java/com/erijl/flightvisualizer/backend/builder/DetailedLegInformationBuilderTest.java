package com.erijl.flightvisualizer.backend.builder;

import com.erijl.flightvisualizer.backend.model.projections.LegRenderDataProjection;
import com.erijl.flightvisualizer.protos.objects.DetailedLegInformation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DetailedLegInformationBuilderTest {

    @Test
    public void testBuildDetailedLegInformation() {
        LegRenderDataProjection projection = mock(LegRenderDataProjection.class);
        when(projection.getAircraftDepartureTimeUtc()).thenReturn(1234);
        when(projection.getAircraftArrivalTimeUtc()).thenReturn(5678);
        when(projection.getAircraftDepartureTimeDateDiffUtc()).thenReturn(0);
        when(projection.getAircraftArrivalTimeDateDiffUtc()).thenReturn(1);
        when(projection.getOperationPeriodWeekdays()).thenReturn("12345");
        when(projection.getAircraftCode()).thenReturn("A380");
        when(projection.getAircraftName()).thenReturn("Airbus A380");
        when(projection.getOperationDate()).thenReturn("2023-12-25");

        DetailedLegInformation result = DetailedLegInformationBuilder.buildDetailedLegInformation(projection);

        assertEquals(1234, result.getDepartureTimeUtc());
        assertEquals(5678, result.getArrivalTimeUtc());
        assertEquals(0, result.getAircraftDepartureTimeDateDiffUtc());
        assertEquals(1, result.getAircraftArrivalTimeDateDiffUtc());
        assertEquals("12345", result.getOperationPeriodWeekdays());
        assertEquals("A380", result.getAircraftCode());
        assertEquals("Airbus A380", result.getAircraftName());
        assertEquals("2023-12-25", result.getOperationDate());
    }

    @Test
    public void testBuildDetailedLegInformationWithNullAircraftName() {
        LegRenderDataProjection projection = mock(LegRenderDataProjection.class);
        when(projection.getAircraftDepartureTimeUtc()).thenReturn(1234);
        when(projection.getAircraftArrivalTimeUtc()).thenReturn(5678);
        when(projection.getAircraftDepartureTimeDateDiffUtc()).thenReturn(0);
        when(projection.getAircraftArrivalTimeDateDiffUtc()).thenReturn(1);
        when(projection.getOperationPeriodWeekdays()).thenReturn("12345");
        when(projection.getAircraftCode()).thenReturn("A380");
        when(projection.getOperationDate()).thenReturn("2023-12-25");
        when(projection.getAircraftName()).thenReturn(null);

        DetailedLegInformation result = DetailedLegInformationBuilder.buildDetailedLegInformation(projection);

        assertEquals("", result.getAircraftName());
    }

    @Test
    public void testBuildDetailedLegInformationList() {
        List<LegRenderDataProjection> projections = new ArrayList<>();
        projections.add(createMockProjection(1234, 5678, 0, 1, "12345", "A380", "Airbus A380", "2023-12-25"));
        projections.add(createMockProjection(9876, 5432, -1, 0, "123", "B777", "Boeing 777", "2024-01-01"));

        List<DetailedLegInformation> results = DetailedLegInformationBuilder.buildDetailedLegInformationList(projections);

        assertEquals(2, results.size());

        DetailedLegInformation firstResult = results.getFirst();
        assertEquals(1234, firstResult.getDepartureTimeUtc());
        assertEquals(5678, firstResult.getArrivalTimeUtc());
        assertEquals(0, firstResult.getAircraftDepartureTimeDateDiffUtc());
        assertEquals(1, firstResult.getAircraftArrivalTimeDateDiffUtc());
        assertEquals("12345", firstResult.getOperationPeriodWeekdays());
        assertEquals("A380", firstResult.getAircraftCode());
        assertEquals("Airbus A380", firstResult.getAircraftName());
        assertEquals("2023-12-25", firstResult.getOperationDate());

        DetailedLegInformation secondResult = results.get(1);
        assertEquals(9876, secondResult.getDepartureTimeUtc());
        assertEquals(5432, secondResult.getArrivalTimeUtc());
        assertEquals(-1, secondResult.getAircraftDepartureTimeDateDiffUtc());
        assertEquals(0, secondResult.getAircraftArrivalTimeDateDiffUtc());
        assertEquals("123", secondResult.getOperationPeriodWeekdays());
        assertEquals("B777", secondResult.getAircraftCode());
        assertEquals("Boeing 777", secondResult.getAircraftName());
        assertEquals("2024-01-01", secondResult.getOperationDate());
    }

    @Test
    public void testBuildDetailedLegInformationList_nullProjections() {
        assertThrows(NullPointerException.class, () -> DetailedLegInformationBuilder.buildDetailedLegInformationList(Collections.singletonList(null)),
                "Expected NullPointerException for null projections list");
    }

    private LegRenderDataProjection createMockProjection(int departureTimeUtc, int arrivalTimeUtc, int departureTimeDateDiffUtc,
                                                         int arrivalTimeDateDiffUtc, String operationPeriodWeekdays, String aircraftCode,
                                                         String aircraftName, String operationDate) {
        LegRenderDataProjection projection = mock(LegRenderDataProjection.class);
        when(projection.getAircraftDepartureTimeUtc()).thenReturn(departureTimeUtc);
        when(projection.getAircraftArrivalTimeUtc()).thenReturn(arrivalTimeUtc);
        when(projection.getAircraftDepartureTimeDateDiffUtc()).thenReturn(departureTimeDateDiffUtc);
        when(projection.getAircraftArrivalTimeDateDiffUtc()).thenReturn(arrivalTimeDateDiffUtc);
        when(projection.getOperationPeriodWeekdays()).thenReturn(operationPeriodWeekdays);
        when(projection.getAircraftCode()).thenReturn(aircraftCode);
        when(projection.getAircraftName()).thenReturn(aircraftName);
        when(projection.getOperationDate()).thenReturn(operationDate);


        return projection;
    }
}
