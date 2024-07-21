package com.erijl.flightvisualizer.backend.util;

import com.erijl.flightvisualizer.backend.model.api.LegResponse;
import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class TimeUtil {

    private static final int MAX_MINUTES_IN_DAY = 1439;
    private static final ZoneId TIMEZONE = ZoneId.of("UTC");

    private TimeUtil() {
    }

    /**
     * Date conversion to the 'ddMMMyy' format used by Lufthansa
     * example:
     * <pre>
     *  24.12.2023 -> 24DEC23
     * </pre>
     *
     * @param date a {@link LocalDate} to be converted
     * @return A formatted {@link String} in the 'ddMMMyy' format
     */
    public static String convertDateToDDMMMYY(LocalDate date) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("ddMMMyy", Locale.ENGLISH);
        String formattedDate = date.format(dateFormat);

        return formattedDate.substring(0, 2) +
                formattedDate.substring(2, 5).toUpperCase() +
                formattedDate.substring(5);
    }

    /**
     * Converts a date string in the 'ddMMMyy' format back to a {@link Date} object.
     * Example:
     * <pre>
     *  24DEZ23 -> 24.12.2023
     * </pre>
     *
     * @param ddMMMyyFormat The date string in the 'ddMMMyy' format to be converted
     * @return A {@link Date} object representing the converted date. Returns null if the string cannot be parsed.
     */
    public static Date convertDDMMMYYToSQLDate(String ddMMMyyFormat) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("ddMMMyy", Locale.ENGLISH);
        return convertDateToSqlDate(LocalDate.parse(ddMMMyyFormat, dateFormat));
    }

    /**
     * Converts a {@link LocalDate} object to a {@link Date} object.
     *
     * @param date The {@link LocalDate} object to be converted
     * @return A {@link Date} object representing the converted date
     */
    public static Date convertDateToSqlDate(LocalDate date) {
        return Date.valueOf(date);
    }

    /**
     * Calculates the duration of a flight leg in minutes.
     *
     * @param legResponse The {@link LegResponse} object containing the departure and arrival times
     * @return The duration of the flight leg in minutes
     */
    public static int calculateDurationInMinutes(LegResponse legResponse) {
        return calculateDurationInMinutes(legResponse.getAircraftDepartureTimeUTC(),
                legResponse.getAircraftArrivalTimeUTC(),
                legResponse.getAircraftArrivalTimeDateDiffUTC());
    }


    /**
     * Converts a {@link Timestamp} to a {@link LocalDate} UTC
     *
     * @param timestamp {@link Timestamp} to be converted
     * @return A {@link LocalDate} representing the converted timestamp, or null if the timestamp is null or has no value
     */
    public static LocalDate convertProtoTimestampToLocalDate(Timestamp timestamp) {
        if (timestamp == null || timestamp.getSeconds() == 0 && timestamp.getNanos() == 0) {
            return null;
        }

        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()).atZone(TIMEZONE).toLocalDate();
    }

    /**
     * Converts a {@link LocalDate} to a {@link Timestamp} UTC
     *
     * @param localDate {@link LocalDate} to be converted
     * @return A {@link Timestamp} representing the converted local date, or null if the local date is null
     */
    public static Timestamp convertLocalDateToProtoTimestamp(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }

        return Timestamp.newBuilder()
                .setSeconds(localDate.atStartOfDay(TIMEZONE).toEpochSecond())
                .setNanos(0)
                .build();
    }

    /**
     * Converts a date string in the 'yyyy-MM-dd' format to a {@link LocalDate} object.
     *
     * @param date The date string in the 'yyyy-MM-dd' format to be converted
     * @return A {@link LocalDate} object representing the converted date
     */
    public static LocalDate convertyyyyMMddStringToUTCLocalDate(String date) {
        return LocalDate.of(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)), Integer.parseInt(date.substring(8)));
    }

    private static int calculateDurationInMinutes(int a, int b, int d) {
        return (MAX_MINUTES_IN_DAY * d) + (b - a);
    }
}
