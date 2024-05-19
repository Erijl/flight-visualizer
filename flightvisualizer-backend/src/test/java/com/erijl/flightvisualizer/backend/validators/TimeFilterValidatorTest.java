package com.erijl.flightvisualizer.backend.validators;

import com.erijl.flightvisualizer.protos.filter.TimeFilter;
import com.erijl.flightvisualizer.protos.objects.DateRange;
import com.erijl.flightvisualizer.protos.objects.TimeRange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.google.protobuf.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TimeFilterValidatorTest {

    @Test
    public void testValidate_validTimeFilter() {
        TimeFilter validFilter = createTimeFilter(1000, 1200, 1684387200L, 1684473600L);

        assertDoesNotThrow(() -> TimeFilterValidator.validate(validFilter));
    }

    @Test
    public void testValidate_nullTimeFilter() {
        assertThrows(IllegalArgumentException.class, () -> TimeFilterValidator.validate(null));
    }

    @Test
    public void testValidate_missingTimeRange() {
        TimeFilter filter = createTimeFilter(null, null, 1684387200L, 1684473600L);
        assertThrows(IllegalArgumentException.class, () -> TimeFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_TimeRangeStartDateAfterEndDate() {
        TimeFilter filter = createTimeFilter(1000, 500, 1684387200L, 1684473600L);
        assertThrows(IllegalArgumentException.class, () -> TimeFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_missingDateRange() {
        TimeFilter filter = createTimeFilter(1000, 1200, null, null);
        assertThrows(IllegalArgumentException.class, () -> TimeFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_invalidTimeRange() {
        TimeFilter filter = createTimeFilter(1500, 1000, 1684387200L, 1684473600L);
        assertThrows(IllegalArgumentException.class, () -> TimeFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_invalidDateRange() {
        TimeFilter filter = createTimeFilter(1000, 1200, 1684473600L, 1684387200L);
        assertThrows(IllegalArgumentException.class, () -> TimeFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_unsetDateRange() {
        TimeFilter filter = createTimeFilter(1000, 1200, 0L, 0L);
        assertThrows(IllegalArgumentException.class, () -> TimeFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_unsetDatesInDateRange() {
        TimeFilter filter = TimeFilter.newBuilder()
                .setDateRange(DateRange.newBuilder()
                .build()).build();
        assertThrows(IllegalArgumentException.class, () -> TimeFilterValidator.validate(filter));
    }

    // Helper method to create TimeFilter instances
    private TimeFilter createTimeFilter(Integer startTime, Integer endTime, Long startDateSeconds, Long endDateSeconds) {
        TimeFilter.Builder builder = TimeFilter.newBuilder();
        if (startTime != null && endTime != null) {
            builder.setTimeRange(TimeRange.newBuilder()
                    .setStart(startTime)
                    .setEnd(endTime)
                    .build());
        }
        if (startDateSeconds != null && endDateSeconds != null) {
            builder.setDateRange(DateRange.newBuilder()
                    .setStart(Timestamp.newBuilder().setSeconds(startDateSeconds).build())
                    .setEnd(Timestamp.newBuilder().setSeconds(endDateSeconds).build())
                    .build());
        }
        return builder.build();
    }
}