package com.erijl.flightvisualizer.backend.validators;

import com.erijl.flightvisualizer.protos.objects.AirportRender;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AirportRenderValidatorTest {

    @Test
    public void testValidateWithNullAirportRender() {
        assertThrows(IllegalArgumentException.class, () -> AirportRenderValidator.validate(null));
    }

    @Test
    public void testValidateWithInvalidIataCode() {
        AirportRender airportRender = AirportRender.newBuilder().setIataCode("AB").build();
        assertThrows(IllegalArgumentException.class, () -> AirportRenderValidator.validate(airportRender));

        AirportRender airportRender2 = AirportRender.newBuilder().setIataCode("").build();
        assertThrows(IllegalArgumentException.class, () -> AirportRenderValidator.validate(airportRender2));
    }

    @Test
    public void testValidateWithValidIataCode() {
        AirportRender airportRender = AirportRender.newBuilder().setIataCode("ABC").build();
        assertDoesNotThrow(() -> AirportRenderValidator.validate(airportRender));
    }
}