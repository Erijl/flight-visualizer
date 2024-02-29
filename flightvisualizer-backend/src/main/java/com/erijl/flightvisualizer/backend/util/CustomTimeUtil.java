package com.erijl.flightvisualizer.backend.util;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

@Component
public class CustomTimeUtil {

    /**
     * Date conversion to the 'ddMMMyy' format used by Lufthansa
     * Example:
     * <pre>
     *  24.12.2023 -> 24DEC23
     * </pre>
     *
     * @param date The {@link Date} to be converted
     * @return A formatted {@link String} in the 'ddMMMyy' format
     */
    public String convertDateToDDMMMYY(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMMyy", Locale.ENGLISH);
        String formattedDate = dateFormat.format(date);

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
     * @throws ParseException if the string cannot be parsed into a date
     */
    public Date convertDDMMMYYToDate(String ddMMMyyFormat) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMMyy", Locale.ENGLISH);
            return dateFormat.parse(ddMMMyyFormat);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public java.sql.Date convertDDMMMYYToSQLDate(String ddMMMyyFormat) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMMyy", Locale.ENGLISH);
            return this.convertDateToSqlDate(dateFormat.parse(ddMMMyyFormat));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LocalDate convertDateToUtcLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.of("UTC"))
                .toLocalDate();
    }

    public LocalDate convertStringToLocalDate(String date) {
        return LocalDate.parse(date);
    }

    public java.sql.Date convertDateToSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }
}
