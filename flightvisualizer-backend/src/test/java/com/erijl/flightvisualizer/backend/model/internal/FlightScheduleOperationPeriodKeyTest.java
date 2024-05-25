package com.erijl.flightvisualizer.backend.model.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FlightScheduleOperationPeriodKeyTest {

    @Test
    public void testConstructorAndGetters() {
        WeekRepresentation operationDaysUtc = new WeekRepresentation("123    ");
        WeekRepresentation operationDaysLt = new WeekRepresentation(" 23 5  ");
        FlightScheduleOperationPeriodKey key = new FlightScheduleOperationPeriodKey(
                "2024-01-01", "2024-02-01", operationDaysUtc,
                "2024-01-15", "2024-02-15", operationDaysLt
        );

        assertEquals("2024-01-01", key.getStartDateUtc());
        assertEquals("2024-02-01", key.getEndDateUtc());
        assertEquals("123    ", key.getOperationDaysUtc().toDaysOfOperationString());
        assertEquals("2024-01-15", key.getStartDateLt());
        assertEquals("2024-02-15", key.getEndDateLt());
        assertEquals(" 23 5  ", key.getOperationDaysLt().toDaysOfOperationString());
    }

    @Test
    public void testDefaultConstructor() {
        FlightScheduleOperationPeriodKey key = new FlightScheduleOperationPeriodKey();

        assertNull(key.getStartDateUtc());
        assertNull(key.getEndDateUtc());
        assertNull(key.getOperationDaysUtc());
        assertNull(key.getStartDateLt());
        assertNull(key.getEndDateLt());
        assertNull(key.getOperationDaysLt());
    }

    @Test
    public void testToString() {
        WeekRepresentation operationDaysUtc = new WeekRepresentation("123    ");
        WeekRepresentation operationDaysLt = new WeekRepresentation(" 23 5  ");
        FlightScheduleOperationPeriodKey key = new FlightScheduleOperationPeriodKey(
                "2024-01-01", "2024-02-01", operationDaysUtc,
                "2024-01-15", "2024-02-15", operationDaysLt
        );

        String expectedString = "FlightScheduleOperationPeriodKey{" +
                "startDateUtc='2024-01-01', endDateUtc='2024-02-01', operationDaysUtc=123    , " +
                "startDateLt='2024-01-15', endDateLt='2024-02-15', operationDaysLt= 23 5  }";

        assertEquals(expectedString, key.toString());
    }
}

