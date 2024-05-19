package com.erijl.flightvisualizer.backend.validators;

import com.erijl.flightvisualizer.protos.objects.LegRender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LegRenderValidatorTest {

    @Test
    public void testValidateWithNullAirportRender() {
        assertThrows(IllegalArgumentException.class, () -> LegRenderValidator.validate(null));
    }

    @Test
    public void testValidateWithInvalidIataCode() {
        LegRender legRender = LegRender.newBuilder().setOriginAirportIataCode("AB").build();
        assertThrows(IllegalArgumentException.class, () -> LegRenderValidator.validate(legRender));

        LegRender legRender2 = LegRender.newBuilder().setOriginAirportIataCode("").build();
        assertThrows(IllegalArgumentException.class, () -> LegRenderValidator.validate(legRender2));
    }

    @Test
    public void testValidateWithValidIataCode() {
        LegRender legRender = LegRender.newBuilder().setOriginAirportIataCode("ABC").setDestinationAirportIataCode("ABC").build();
        assertDoesNotThrow(() -> LegRenderValidator.validate(legRender));
    }
}
