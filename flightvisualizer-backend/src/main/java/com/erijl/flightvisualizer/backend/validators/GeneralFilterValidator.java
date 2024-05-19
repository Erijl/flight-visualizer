package com.erijl.flightvisualizer.backend.validators;

import com.erijl.flightvisualizer.protos.enums.AirportDisplayType;
import com.erijl.flightvisualizer.protos.enums.RouteDisplayType;
import com.erijl.flightvisualizer.protos.filter.GeneralFilter;

public class GeneralFilterValidator {

    private GeneralFilterValidator() {
    }

    /**
     * Validates the GeneralFilter object.
     *
     * @param generalFilter the GeneralFilter object to validate
     * @throws IllegalArgumentException if the validation fails
     */
    public static void validate(GeneralFilter generalFilter) {
        if (generalFilter == null) {
            throw new IllegalArgumentException("GeneralFilter cannot be null");
        }

        validateAirportDisplayType(generalFilter.getAirportDisplayType());
        validateRouteDisplayType(generalFilter.getRouteDisplayType());
    }

    private static void validateAirportDisplayType(AirportDisplayType airportDisplayType) {
        if (airportDisplayType == null) {
            throw new IllegalArgumentException("AirportDisplayType cannot be null");
        }
    }

    private static void validateRouteDisplayType(RouteDisplayType routeDisplayType) {
        if (routeDisplayType == null) {
            throw new IllegalArgumentException("RouteDisplayType cannot be null");
        }
    }
}