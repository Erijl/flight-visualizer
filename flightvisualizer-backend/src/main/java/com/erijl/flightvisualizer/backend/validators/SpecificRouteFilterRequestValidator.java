package com.erijl.flightvisualizer.backend.validators;

import com.erijl.flightvisualizer.protos.filter.SpecificRouteFilterRequest;

public class SpecificRouteFilterRequestValidator {

    public static void validate(SpecificRouteFilterRequest request) {
        if (request == null || !request.hasTimeFilter() || !request.hasLegRender()) {
            throw new IllegalArgumentException("Request is not valid");
        }

        TimeFilterValidator.validate(request.getTimeFilter());
        LegRenderValidator.validate(request.getLegRender());
    }
}
