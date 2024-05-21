package com.erijl.flightvisualizer.backend.util;

import com.erijl.flightvisualizer.backend.builder.UrlBuilder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlBuilderTest {

    @Test
    public void testAccessToken() {
        UrlBuilder urlBuilder = new UrlBuilder("https://test.com/");
        String url = urlBuilder.accessToken().getUrl();
        assertEquals("https://test.com/oauth/token", url);
    }

    @Test
    public void testFilterAirlineCodes() {
        UrlBuilder urlBuilder = new UrlBuilder("https://test.com/");
        String url = urlBuilder.flightSchedule().filterAirlineCodes("AA").getUrl();
        assertEquals("https://test.com/flight-schedules/flightschedules/passenger?airlines=AA&timeMode=UTC", url);
    }

    @Test
    public void testFilterFlightNumberRanges() {
        UrlBuilder urlBuilder = new UrlBuilder("https://test.com/");
        String url = urlBuilder.flightSchedule().filterAirlineCodes("AA").filterFlightNumberRanges("100-200").getUrl();
        assertEquals("https://test.com/flight-schedules/flightschedules/passenger?airlines=AA&flightNumberRanges=100-200&timeMode=UTC", url);
    }

    @Test
    public void testFilterStartDate() {
        UrlBuilder urlBuilder = new UrlBuilder("https://test.com/");
        String url = urlBuilder.flightSchedule().filterAirlineCodes("AA").filterStartDate("2022-01-01").getUrl();
        assertEquals("https://test.com/flight-schedules/flightschedules/passenger?airlines=AA&startDate=2022-01-01&timeMode=UTC", url);
    }

    @Test
    public void testFilterEndDate() {
        UrlBuilder urlBuilder = new UrlBuilder("https://test.com/");
        String url = urlBuilder.flightSchedule().filterAirlineCodes("AA").filterEndDate("2022-12-31").getUrl();
        assertEquals("https://test.com/flight-schedules/flightschedules/passenger?airlines=AA&endDate=2022-12-31&timeMode=UTC", url);
    }

    @Test
    public void testFilterDaysOfOperation() {
        UrlBuilder urlBuilder = new UrlBuilder("https://test.com/");
        String url = urlBuilder.flightSchedule().filterAirlineCodes("AA").filterDaysOfOperation("12345").getUrl();
        assertEquals("https://test.com/flight-schedules/flightschedules/passenger?airlines=AA&daysOfOperation=12345&timeMode=UTC", url);
    }

    @Test
    public void testFilterOrigin() {
        UrlBuilder urlBuilder = new UrlBuilder("https://test.com/");
        String url = urlBuilder.flightSchedule().filterAirlineCodes("AA").filterOrigin("JFK").getUrl();
        assertEquals("https://test.com/flight-schedules/flightschedules/passenger?airlines=AA&origin=JFK&timeMode=UTC", url);
    }

    @Test
    public void testFilterDestination() {
        UrlBuilder urlBuilder = new UrlBuilder("https://test.com/");
        String url = urlBuilder.flightSchedule().filterAirlineCodes("AA").filterDestination("LAX").getUrl();
        assertEquals("https://test.com/flight-schedules/flightschedules/passenger?airlines=AA&destination=LAX&timeMode=UTC", url);
    }

    @Test
    public void testFilterAircraftTypes() {
        UrlBuilder urlBuilder = new UrlBuilder("https://test.com/");
        String url = urlBuilder.flightSchedule().filterAirlineCodes("AA").filterAircraftTypes(Arrays.asList("A320", "B737")).getUrl();
        assertEquals("https://test.com/flight-schedules/flightschedules/passenger?airlines=AA&aircraftTypes=[A320, B737]&timeMode=UTC", url);
    }
}