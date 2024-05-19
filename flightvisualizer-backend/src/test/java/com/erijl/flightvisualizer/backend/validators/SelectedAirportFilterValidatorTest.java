package com.erijl.flightvisualizer.backend.validators;

import com.erijl.flightvisualizer.protos.filter.SelectedAirportFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SelectedAirportFilterValidatorTest {

    @Test
    public void testValidate_wrongCodeLength() {
        SelectedAirportFilter filter = SelectedAirportFilter.newBuilder()
                .setIataCode("AA")
                .build();
        assertThrows(IllegalArgumentException.class, () -> SelectedAirportFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_nullFilter() {
        assertThrows(IllegalArgumentException.class, () -> SelectedAirportFilterValidator.validate(null));
    }
}
