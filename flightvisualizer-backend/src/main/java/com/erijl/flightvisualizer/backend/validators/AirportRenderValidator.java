package com.erijl.flightvisualizer.backend.validators;

import com.erijl.flightvisualizer.protos.objects.AirportRender;

public class AirportRenderValidator {

    private AirportRenderValidator() {
    }

    /**
     * Validates an {@link AirportRender}
     *
     * @param airportRender the {@link AirportRender} to validate
     * @throws IllegalArgumentException if the validation fails
     */
    public static void validate(AirportRender airportRender) {
        if(airportRender == null) {
            throw new IllegalArgumentException("AirportRender cannot be null");
        }

        if(airportRender.getIataCode().isEmpty() || airportRender.getIataCode().length() != 3) {
            throw new IllegalArgumentException("AirportRender IATA Code is not valid");
        }
    }
}
