package com.erijl.flightvisualizer.backend.model.enums;

public enum LocationType {
    AIRPORT("Airport"),
    RAILWAY_STATION("Rail Station"),
    BUS_STATION("Bus Station");

    private final String locationType;

    LocationType(String locationType) {
        this.locationType = locationType;
    }

    public boolean equalsName(String otherName) {
        return locationType.equals(otherName);
    }
}
