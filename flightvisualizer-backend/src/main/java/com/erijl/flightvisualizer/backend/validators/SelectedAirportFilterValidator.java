package com.erijl.flightvisualizer.backend.validators;

import com.erijl.flightvisualizer.protos.filter.SelectedAirportFilter;

public class SelectedAirportFilterValidator {

    private static final int MAX_IATA_CODE_LENGTH = 3;

    private SelectedAirportFilterValidator() {}

    /**
     * Validates the given SelectedAirportFilter object.
     *
     * @param selectedAirportFilter SelectedAirportFilter object to validate
     * @throws IllegalArgumentException if the validation fails
     */
    public static void validate(SelectedAirportFilter selectedAirportFilter) {
        if (selectedAirportFilter == null) {
            throw new IllegalArgumentException("SelectedAirportFilter cannot be null");
        }


        String iataCode = selectedAirportFilter.getIataCode();
        if (!iataCode.isBlank() && iataCode.length() != MAX_IATA_CODE_LENGTH) {
            throw new IllegalArgumentException("IATA code must be 3 characters long");
        }
    }
}
