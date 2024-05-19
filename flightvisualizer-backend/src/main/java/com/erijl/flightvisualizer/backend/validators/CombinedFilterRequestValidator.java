package com.erijl.flightvisualizer.backend.validators;

import com.erijl.flightvisualizer.protos.filter.CombinedFilterRequest;

public class CombinedFilterRequestValidator {

    private CombinedFilterRequestValidator() {
    }

    /**
     * Validates the CombinedFilterRequest object
     *
     * @param combinedFilterRequest CombinedFilterRequest object to validate
     * @throws IllegalArgumentException if the validation fails
     */
    public static void validate(CombinedFilterRequest combinedFilterRequest) {
        if (combinedFilterRequest == null) {
            throw new IllegalArgumentException("CombinedFilterRequest cannot be null");
        }

        if (!combinedFilterRequest.hasGeneralFilter() || !combinedFilterRequest.hasRouteFilter() || !combinedFilterRequest.hasSelectedAirportFilter() || !combinedFilterRequest.hasTimeFilter()) {
            throw new IllegalArgumentException("General/Route/SelectedAirport/Time filter does not exist");
        }

        GeneralFilterValidator.validate(combinedFilterRequest.getGeneralFilter());
        RouteFilterValidator.validate(combinedFilterRequest.getRouteFilter());
        SelectedAirportFilterValidator.validate(combinedFilterRequest.getSelectedAirportFilter());
        TimeFilterValidator.validate(combinedFilterRequest.getTimeFilter());
    }
}
