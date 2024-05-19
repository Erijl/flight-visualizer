package com.erijl.flightvisualizer.backend.validators;

import com.erijl.flightvisualizer.protos.enums.RouteFilterType;
import com.erijl.flightvisualizer.protos.filter.RouteFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class RouteFilterValidatorTest {

    @Test
    public void testValidate_validDurationFilter() {
        RouteFilter filter = RouteFilter.newBuilder()
                .setRouteFilterType(RouteFilterType.DURATION)
                .setStart(30)
                .setEnd(600)
                .build();
        assertDoesNotThrow(() -> RouteFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_validDistanceFilter() {
        RouteFilter filter = RouteFilter.newBuilder()
                .setRouteFilterType(RouteFilterType.DISTANCE)
                .setStart(500)
                .setEnd(10000)
                .build();
        assertDoesNotThrow(() -> RouteFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_nullRouteFilter() {
        assertThrows(IllegalArgumentException.class, () -> RouteFilterValidator.validate(null));
    }

    @Test
    public void testValidate_negativeStartDuration() {
        RouteFilter filter = RouteFilter.newBuilder()
                .setRouteFilterType(RouteFilterType.DURATION)
                .setStart(-10)
                .setEnd(60)
                .build();
        assertThrows(IllegalArgumentException.class, () -> RouteFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_negativeEndDuration() {
        RouteFilter filter = RouteFilter.newBuilder()
                .setRouteFilterType(RouteFilterType.DURATION)
                .setStart(30)
                .setEnd(-60)
                .build();
        assertThrows(IllegalArgumentException.class, () -> RouteFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_excessiveStartDuration() {
        RouteFilter filter = RouteFilter.newBuilder()
                .setRouteFilterType(RouteFilterType.DURATION)
                .setStart(1441)
                .setEnd(5000)
                .build();
        assertThrows(IllegalArgumentException.class, () -> RouteFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_excessiveEndDuration() {
        RouteFilter filter = RouteFilter.newBuilder()
                .setRouteFilterType(RouteFilterType.DURATION)
                .setStart(5000)
                .setEnd(1441)
                .build();
        assertThrows(IllegalArgumentException.class, () -> RouteFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_startDurationAfterEndDuration() {
        RouteFilter filter = RouteFilter.newBuilder()
                .setRouteFilterType(RouteFilterType.DURATION)
                .setStart(600)
                .setEnd(30)
                .build();
        assertThrows(IllegalArgumentException.class, () -> RouteFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_negativeStartDistance() {
        RouteFilter filter = RouteFilter.newBuilder()
                .setRouteFilterType(RouteFilterType.DISTANCE)
                .setStart(-100)
                .setEnd(1000)
                .build();
        assertThrows(IllegalArgumentException.class, () -> RouteFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_negativeEndDistance() {
        RouteFilter filter = RouteFilter.newBuilder()
                .setRouteFilterType(RouteFilterType.DISTANCE)
                .setStart(500)
                .setEnd(-1000)
                .build();
        assertThrows(IllegalArgumentException.class, () -> RouteFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_excessiveDistance() {
        RouteFilter filter = RouteFilter.newBuilder()
                .setRouteFilterType(RouteFilterType.DISTANCE)
                .setStart(500)
                .setEnd(20001)
                .build();
        assertThrows(IllegalArgumentException.class, () -> RouteFilterValidator.validate(filter));
    }

    @Test
    public void testValidate_startDistanceAfterEndDistance() {
        RouteFilter filter = RouteFilter.newBuilder()
                .setRouteFilterType(RouteFilterType.DISTANCE)
                .setStart(10000)
                .setEnd(500)
                .build();
        assertThrows(IllegalArgumentException.class, () -> RouteFilterValidator.validate(filter));
    }
}