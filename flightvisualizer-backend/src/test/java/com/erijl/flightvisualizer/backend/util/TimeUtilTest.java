package com.erijl.flightvisualizer.backend.util;

import com.erijl.flightvisualizer.backend.model.api.LegResponse;
import com.erijl.flightvisualizer.backend.model.entities.FlightScheduleLeg;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TimeUtilTest {

    @Test
    void testConvertDateToDDMMMYYLocalDate() {
        assertEquals("11AUG23", TimeUtil.convertDateToDDMMMYY(
                LocalDate.of(
                        2023,
                        8,
                        11
                )
        ));

        assertEquals("01DEC24", TimeUtil.convertDateToDDMMMYY(
                LocalDate.of(
                        2024,
                        12,
                        1
                )
        ));
    }

    @Test
    void testCalculateDurationInMinutesSameDay() {
        LegResponse legResponse = new LegResponse();
        legResponse.setAircraftArrivalTimeDateDiffUTC(0);
        legResponse.setAircraftDepartureTimeUTC(300);
        legResponse.setAircraftArrivalTimeUTC(1000);

        FlightScheduleLeg flightScheduleLeg = new FlightScheduleLeg();
        flightScheduleLeg.setAircraftArrivalTimeDateDiffUtc(0);
        flightScheduleLeg.setAircraftDepartureTimeUtc(300);
        flightScheduleLeg.setAircraftArrivalTimeUtc(1000);

        // Departs at 300 and arrives at 1000, duraion = 700 minutes

        assertEquals(700, TimeUtil.calculateDurationInMinutes(legResponse));
    }

    @Test
    void testCalculateDurationInMinutesDifferentDay() {
        LegResponse legResponse = new LegResponse();
        legResponse.setAircraftArrivalTimeDateDiffUTC(1);
        legResponse.setAircraftDepartureTimeUTC(300);
        legResponse.setAircraftArrivalTimeUTC(1000);

        FlightScheduleLeg flightScheduleLeg = new FlightScheduleLeg();
        flightScheduleLeg.setAircraftArrivalTimeDateDiffUtc(1);
        flightScheduleLeg.setAircraftDepartureTimeUtc(300);
        flightScheduleLeg.setAircraftArrivalTimeUtc(1000);

        assertEquals(2139, TimeUtil.calculateDurationInMinutes(legResponse));
    }

    /*
     * Given a = 300, b = 1000, and d = 7.
     *
     * Using the formula:
     * Time difference = (1439 * d) + (b - a)
     *
     * Substituting the values:
     * Time difference = (1439 * 7) + (1000 - 300)
     *                 = 10080 + 700
     *                 = 10773 minutes
     */
    @Test
    void testCalculateDurationInMinutesNextWeek() {
        LegResponse legResponse = new LegResponse();
        legResponse.setAircraftArrivalTimeDateDiffUTC(7);
        legResponse.setAircraftDepartureTimeUTC(300);
        legResponse.setAircraftArrivalTimeUTC(1000);

        FlightScheduleLeg flightScheduleLeg = new FlightScheduleLeg();
        flightScheduleLeg.setAircraftArrivalTimeDateDiffUtc(7);
        flightScheduleLeg.setAircraftDepartureTimeUtc(300);
        flightScheduleLeg.setAircraftArrivalTimeUtc(1000);

        assertEquals(10773, TimeUtil.calculateDurationInMinutes(legResponse));
    }

    @Test
    void testConvertProtoTimestampToLocalDate() {
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(1620000000)
                .setNanos(0)
                .build();

        assertEquals("2021-05-03", TimeUtil.convertProtoTimestampToLocalDate(timestamp).toString());
    }

    @Test
    void testConvertProtoTimestampToLocalDateWithDefault() {
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(0)
                .setNanos(0)
                .build();

        assertNull(TimeUtil.convertProtoTimestampToLocalDate(timestamp));
    }
}
