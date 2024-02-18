package com.erijl.flightvisualizer.backend.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        assertEquals("01DEZ24", customTimeUtil.convertDateToDDMMMYY(
                new GregorianCalendar(
                        2024,
                        Calendar.JANUARY,
                        1
                ).getTime()
        ));
    }
}
