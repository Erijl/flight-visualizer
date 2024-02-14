package com.erijl.flightvisualizer.backend.util;

import com.erijl.flightvisualizer.backend.enums.TimeMode;

import java.util.List;

public class UrlBuilder {

    private final StringBuilder url;

    public UrlBuilder(String baseUrl) {
        url = new StringBuilder().append(baseUrl);
    }

    public EndpointBuilder endpoint(String endpoint) {
        switch (endpoint) {
            case "flight-schedules":
                url.append("flight-schedules/flightschedules/passenger?");
                break;
        }

        return new EndpointBuilder();
    }

    public class EndpointBuilder {

        public FilterBuilder filterAirlineCodes(List<String> airlineCodes) {
            url.append("airlines=").append(airlineCodes);
            return new FilterBuilder();
        }
    }

    public class FilterBuilder {

        public FilterBuilder filterFlightNumberRanges(String flightNumberRanges) {
            url.append("&flightNumberRanges=").append(flightNumberRanges);
            return this;
        }

        public FilterBuilder filterStartDate(String startDate) {
            url.append("&startDate=").append(startDate);
            return this;
        }

        public FilterBuilder filterEndDate(String endDate) {
            url.append("&endDate=").append(endDate);
            return this;
        }

        public FilterBuilder filterDaysOfOperation(String daysOfOperation) {
            url.append("&daysOfOperation=").append(daysOfOperation);
            return this;
        }

        public FilterBuilder filterTimeMode(TimeMode timeMode) {
            url.append("&timeMode=").append(timeMode.getTimeZoneString());
            return this;
        }

        public FilterBuilder filterOrigin(String origin) {
            url.append("&origin=").append(origin);
            return this;
        }

        public FilterBuilder filterDestination(String destination) {
            url.append("&destination=").append(destination);
            return this;
        }

        public FilterBuilder filterAircraftTypes(List<String> aircraftTypes) {
            url.append("&aircraftTypes=").append(aircraftTypes);
            return this;
        }
    }
}
