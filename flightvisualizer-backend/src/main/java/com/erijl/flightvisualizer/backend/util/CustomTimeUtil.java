package com.erijl.flightvisualizer.backend.util;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Component
public class CustomTimeUtil {

    /**
     * Date conversion to the 'ddMMMyy' format used in aviation
     * Example:
     * <pre>
     *  24.12.2023 -> 24DEZ23
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
}
