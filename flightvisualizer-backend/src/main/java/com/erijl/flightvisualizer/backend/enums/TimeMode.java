package com.erijl.flightvisualizer.backend.enums;

public enum TimeMode {

    UTC("UTC"),
    LT("LT");

    private final String timeZoneString;

    TimeMode(String timeZoneString) {
        this.timeZoneString = timeZoneString;
    }

    public String getTimeZoneString() {
        return this.timeZoneString;
    }

}
