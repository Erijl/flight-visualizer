package com.erijl.flightvisualizer.backend.util;

import com.erijl.flightvisualizer.backend.model.api.LegResponse;
import com.erijl.flightvisualizer.backend.model.entities.FlightScheduleLeg;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CustomTimeUtilTest {

    @Test
    void testConvertDateToDDMMMYY() {
        CustomTimeUtil customTimeUtil = new CustomTimeUtil();

        assertEquals("11AUG23", customTimeUtil.convertDateToDDMMMYY(
                new GregorianCalendar(
                        2023,
                        Calendar.AUGUST,
                        11
                ).getTime()
        ));

        assertEquals("01DEC24", customTimeUtil.convertDateToDDMMMYY(
                new GregorianCalendar(
                        2024,
                        Calendar.DECEMBER,
                        1
                ).getTime()
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

        assertEquals(700, CustomTimeUtil.calculateDurationInMinutes(legResponse));
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

        assertEquals(2139, CustomTimeUtil.calculateDurationInMinutes(legResponse));
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

        assertEquals(10773, CustomTimeUtil.calculateDurationInMinutes(legResponse));
    }

    @Test
    void testConvertProtoTimestampToLocalDate() {
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(1620000000)
                .setNanos(0)
                .build();

        assertEquals("2021-05-03", CustomTimeUtil.convertProtoTimestampToLocalDate(timestamp).toString());
    }

    @Test
    void testConvertProtoTimestampToLocalDateWithDefault() {
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(0)
                .setNanos(0)
                .build();

        assertNull(CustomTimeUtil.convertProtoTimestampToLocalDate(timestamp));
    }
}
