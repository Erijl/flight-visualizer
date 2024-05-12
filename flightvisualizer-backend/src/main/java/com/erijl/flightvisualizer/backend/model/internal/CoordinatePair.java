package com.erijl.flightvisualizer.backend.model.internal;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CoordinatePair {
    private BigDecimal originLongitude;
    private BigDecimal destinationLongitude;

    public CoordinatePair(BigDecimal originLongitude, BigDecimal destinationLongitude) {
        this.originLongitude = originLongitude;
        this.destinationLongitude = destinationLongitude;
    }

    public CoordinatePair() {
    }
}
