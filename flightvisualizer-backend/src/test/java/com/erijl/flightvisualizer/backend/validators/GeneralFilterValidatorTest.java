package com.erijl.flightvisualizer.backend.validators;


import com.erijl.flightvisualizer.protos.enums.AirportDisplayType;
import com.erijl.flightvisualizer.protos.enums.RouteDisplayType;
import com.erijl.flightvisualizer.protos.filter.GeneralFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GeneralFilterValidatorTest {

    @Test
    public void testValidate_validGeneralFilter() {
        GeneralFilter filter = GeneralFilter.newBuilder()
                .setAirportDisplayType(AirportDisplayType.AIRPORTDISPLAYTYPE_ALL)
                .setRouteDisplayType(RouteDisplayType.ROUTEDISPLAYTYPE_ALL)
                .build();
        assertDoesNotThrow(() -> GeneralFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_nullGeneralFilter() {
        assertThrows(IllegalArgumentException.class, () -> GeneralFilterValidator.validate(null));
    }
}