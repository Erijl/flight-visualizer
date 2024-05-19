package com.erijl.flightvisualizer.backend.validators;

import com.erijl.flightvisualizer.protos.filter.TimeFilter;
import com.erijl.flightvisualizer.protos.objects.DateRange;
import com.erijl.flightvisualizer.protos.objects.TimeRange;

public class TimeFilterValidator {

    private TimeFilterValidator() {
    }

    /**
     * Validates the TimeFilter object
     *
     * @param timeFilter TimeFilter object to validate
     * @throws IllegalArgumentException if the validation fails
     */
    public static void validate(TimeFilter timeFilter) {
        if (timeFilter == null) {
            throw new IllegalArgumentException("TimeFilter cannot be null");
        }

        if (!timeFilter.hasTimeRange() || !timeFilter.hasDateRange()) {
            throw new IllegalArgumentException("Time/Date range does not exist");
        }

        validateTimeRange(timeFilter.getTimeRange());
        validateDateRange(timeFilter.getDateRange());
    }

    private static void validateTimeRange(TimeRange timeRange) {
        if (!isTimeValid(timeRange.getStart()) || !isTimeValid(timeRange.getEnd())) {
            throw new IllegalArgumentException("Time is outside of range");
        }

        if (timeRange.getStart() > timeRange.getEnd()) {
            throw new IllegalArgumentException("Start time is after end time");
        }
    }

    private static void validateDateRange(DateRange dateRange) {
        if (!dateRange.hasStart() && !dateRange.hasEnd()) {
            throw new IllegalArgumentException("Neither start or end is initialized in DateRange");
        }

        if (dateRange.getEnd().getSeconds() == 0 && dateRange.getStart().getSeconds() == 0) {
            throw new IllegalArgumentException("Neither start or end is set in DateRange");
        }

        if (dateRange.getStart().getSeconds() > dateRange.getEnd().getSeconds() && dateRange.getEnd().getSeconds() != 0) {
            throw new IllegalArgumentException("Start date is after end date");
        }
    }

    private static boolean isTimeValid(Integer time) {
        return time >= 0 && time <= 1439;
    }
}
