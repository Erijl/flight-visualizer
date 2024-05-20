package com.erijl.flightvisualizer.backend.util;

import com.erijl.flightvisualizer.backend.model.entities.FlightScheduleOperationPeriod;

public class ComparisonUtil {

    private ComparisonUtil() {
    }

    public static boolean isSamePeriod(FlightScheduleOperationPeriod firstPeriod, FlightScheduleOperationPeriod secondPeriod) {
        return firstPeriod.getStartDateUtc().equals(secondPeriod.getStartDateUtc()) &&
                firstPeriod.getEndDateUtc().equals(secondPeriod.getEndDateUtc()) &&
                firstPeriod.getOperationDaysUtc().equals(secondPeriod.getOperationDaysUtc()) &&
                firstPeriod.getStartDateLt().equals(secondPeriod.getStartDateLt()) &&
                firstPeriod.getEndDateLt().equals(secondPeriod.getEndDateLt()) &&
                firstPeriod.getOperationDaysLt().equals(secondPeriod.getOperationDaysLt());
    }
}
