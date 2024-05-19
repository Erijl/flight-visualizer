package com.erijl.flightvisualizer.backend.model;

import com.erijl.flightvisualizer.backend.model.entities.WeekRepresentation;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

public class WeekRepresentationTest {

    @Test
    public void testConversionStringToWeekRepresentation() {
        String daysOfOperation = "1 3  67";
        WeekRepresentation weekRepresentation = new WeekRepresentation(daysOfOperation);

        assertTrue(weekRepresentation.isMonday());
        assertFalse(weekRepresentation.isTuesday());
        assertTrue(weekRepresentation.isWednesday());
        assertFalse(weekRepresentation.isThursday());
        assertFalse(weekRepresentation.isFriday());
        assertTrue(weekRepresentation.isSaturday());
        assertTrue(weekRepresentation.isSunday());
    }

    @Test
    public void testConversionDateToWeekRepresentation() {
        Date date = new GregorianCalendar(
                2024,
                Calendar.JANUARY,
                1
        ).getTime();

        WeekRepresentation weekRepresentation = new WeekRepresentation(date);

        assertTrue(weekRepresentation.isMonday());
        assertFalse(weekRepresentation.isTuesday());
        assertFalse(weekRepresentation.isWednesday());
        assertFalse(weekRepresentation.isThursday());
        assertFalse(weekRepresentation.isFriday());
        assertFalse(weekRepresentation.isSaturday());
        assertFalse(weekRepresentation.isSunday());
    }

    @Test
    public void testConversionWeekRepresentationToString() {
        WeekRepresentation weekRepresentation = new WeekRepresentation();

        weekRepresentation.setMonday(true);
        weekRepresentation.setFriday(true);
        weekRepresentation.setSaturday(true);

        String daysOfOperation = weekRepresentation.toDaysOfOperationString();
        assertTrue(daysOfOperation.contains("1"));
        assertFalse(daysOfOperation.contains("2"));
        assertFalse(daysOfOperation.contains("3"));
        assertFalse(daysOfOperation.contains("4"));
        assertTrue(daysOfOperation.contains("5"));
        assertTrue(daysOfOperation.contains("6"));
        assertFalse(daysOfOperation.contains("7"));
    }
}
