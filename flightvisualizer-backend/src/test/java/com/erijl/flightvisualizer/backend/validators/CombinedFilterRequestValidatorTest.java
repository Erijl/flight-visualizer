package com.erijl.flightvisualizer.backend.validators;

import com.erijl.flightvisualizer.protos.filter.CombinedFilterRequest;
import com.erijl.flightvisualizer.protos.filter.RouteFilter;
import com.erijl.flightvisualizer.protos.filter.SelectedAirportFilter;
import com.erijl.flightvisualizer.protos.filter.TimeFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CombinedFilterRequestValidatorTest {

    @Test
    public void testValidate_nullCombinedFilterRequest() {
        assertThrows(IllegalArgumentException.class, () -> CombinedFilterRequestValidator.validate(null));
    }

    @Test
    public void testValidate_missingGeneralFilter() {
        CombinedFilterRequest filter = CombinedFilterRequest.newBuilder()
                .setRouteFilter(RouteFilter.newBuilder().build())
                .setSelectedAirportFilter(SelectedAirportFilter.newBuilder().build())
                .setTimeFilter(TimeFilter.newBuilder().build())
                .build();
        assertThrows(IllegalArgumentException.class, () -> CombinedFilterRequestValidator.validate(filter));
    }

}