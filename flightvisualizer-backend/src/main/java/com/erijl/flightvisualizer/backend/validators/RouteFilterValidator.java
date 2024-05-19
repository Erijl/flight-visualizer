package com.erijl.flightvisualizer.backend.validators;

import com.erijl.flightvisualizer.protos.enums.RouteFilterType;
import com.erijl.flightvisualizer.protos.filter.RouteFilter;

public class RouteFilterValidator {

    private static final int MAX_DURATION = 2500;
    private static final int MAX_DISTANCE = 20000;

    private RouteFilterValidator() {}

    /**
     * Validates the given RouteFilter object.
     *
     * @param routeFilter RouteFilter object to validate
     * @throws IllegalArgumentException if the validation fails
     */
    public static void validate(RouteFilter routeFilter) {
        if (routeFilter == null) {
            throw new IllegalArgumentException("RouteFilter cannot be null");
        }

        RouteFilterType type = routeFilter.getRouteFilterType();
        int start = routeFilter.getStart();
        int end = routeFilter.getEnd();

        switch (type) {
            case DURATION:
                validateDuration(start, end);
                break;
            case DISTANCE:
                validateDistance(start, end);
                break;
            default:
                throw new IllegalArgumentException("Invalid RouteFilterType");
        }
    }

    private static void validateDuration(int start, int end) {
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("Duration values cannot be negative");
        }
        if (start > MAX_DURATION || end > MAX_DURATION) {
            throw new IllegalArgumentException("Duration values are unrealistic");
        }
        if (start > end) {
            throw new IllegalArgumentException("Start duration cannot be greater than end duration");
        }
    }

    private static void validateDistance(int start, int end) {
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("Distance values cannot be negative");
        }
        if (end > MAX_DISTANCE) { // Maximum realistic distance is 20,000 km
            throw new IllegalArgumentException("Distance value is unrealistic");
        }
        if (start > end) {
            throw new IllegalArgumentException("Start distance cannot be greater than end distance");
        }
    }
}
