package com.erijl.flightvisualizer.backend.validators;

import com.erijl.flightvisualizer.protos.objects.LegRender;

public class LegRenderValidator {

    public static void validate(LegRender legRender) {
        if (legRender == null) {
            throw new IllegalArgumentException("Request is not valid");
        }

        if(legRender.getOriginAirportIataCode().isBlank() || legRender.getOriginAirportIataCode().length() != 3) {
            throw new IllegalArgumentException("Origin IATA code is not valid");
        }

        if(legRender.getDestinationAirportIataCode().isBlank() || legRender.getDestinationAirportIataCode().length() != 3) {
            throw new IllegalArgumentException("Destination IATA code is not valid");
        }
    }
}
