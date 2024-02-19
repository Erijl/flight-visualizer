package com.erijl.flightvisualizer.backend.util;

import com.erijl.flightvisualizer.backend.enums.TimeMode;
import com.erijl.flightvisualizer.backend.model.FlightSchedule;

import java.util.List;

public class UrlBuilder {

    private final StringBuilder url;

    public UrlBuilder(String baseUrl) {
        url = new StringBuilder().append(baseUrl);
    }


    public TokenBuilder accessToken() {
        url.append("oauth/token");
        return new TokenBuilder();
    }

    public FlightScheduleEndpointBuilder flightSchedule() {
        url.append("flight-schedules/flightschedules/passenger?");
        return new FlightScheduleEndpointBuilder();
    }

    public AirlineEndpointBuilder airline() {
        url.append("mds-references/airlines/");
        return new AirlineEndpointBuilder();
    }

    public AircraftEndpointBuilder aircraft() {
        url.append("mds-references/aircraft/");
        return new AircraftEndpointBuilder();
    }

    public class AirlineEndpointBuilder {

        public AirlineEndpointBuilder filterForAirline(String airline) {
            url.append(airline);
            return this;
        }

        public String getUrl() {
            url.append("?lang=EN");
            return url.toString();
        }
    }

    public class AircraftEndpointBuilder {

        public AircraftEndpointBuilder filterForAircraft(String iataAircraftCode) {
            url.append(iataAircraftCode);
            return this;
        }

        public String getUrl() {
            url.append("?lang=EN");
            return url.toString();
        }
    }

    public class FlightScheduleEndpointBuilder {

        public FlightScheduleFilterBuilder filterAirlineCodes(String airlineCodes) {
            url.append("airlines=").append(airlineCodes);
            return new FlightScheduleFilterBuilder();
        }
    }

    public class FlightScheduleFilterBuilder {

        public FlightScheduleFilterBuilder filterFlightNumberRanges(String flightNumberRanges) {
            url.append("&flightNumberRanges=").append(flightNumberRanges);
            return this;
        }

        public FlightScheduleFilterBuilder filterStartDate(String startDate) {
            url.append("&startDate=").append(startDate);
            return this;
        }

        public FlightScheduleFilterBuilder filterEndDate(String endDate) {
            url.append("&endDate=").append(endDate);
            return this;
        }

        public FlightScheduleFilterBuilder filterDaysOfOperation(String daysOfOperation) {
            url.append("&daysOfOperation=").append(daysOfOperation);
            return this;
        }

        public FlightScheduleFilterBuilder filterOrigin(String origin) {
            url.append("&origin=").append(origin);
            return this;
        }

        public FlightScheduleFilterBuilder filterDestination(String destination) {
            url.append("&destination=").append(destination);
            return this;
        }

        public FlightScheduleFilterBuilder filterAircraftTypes(List<String> aircraftTypes) {
            url.append("&aircraftTypes=").append(aircraftTypes);
            return this;
        }

        public String getUrl() {
            url.append("&timeMode=UTC");
            return url.toString();
        }
    }

    public class TokenBuilder {

        public String getUrl() {
            return url.toString();
        }

    }
}
