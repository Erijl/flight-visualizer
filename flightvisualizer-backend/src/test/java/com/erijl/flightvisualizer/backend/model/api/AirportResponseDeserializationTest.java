package com.erijl.flightvisualizer.backend.model.api;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AirportResponseDeserializationTest {

    private final Gson gson = new Gson();

    @Test
    public void testDeserialization_validJson() {
        String json = """
            {
              "AirportResource": {
                "Airports": {
                  "Airport": {
                    "AirportCode": "JFK",
                    "Position": {
                      "Coordinate": {
                        "Latitude": 40.6413,
                        "Longitude": -73.7781
                      }
                    },
                    "CityCode": "NYC",
                    "CountryCode": "US",
                    "LocationType": "Airport",
                    "Names": {
                      "Name": {
                        "@LanguageCode": "en-GB",
                        "$": "John F. Kennedy International Airport"
                      }
                    },
                    "UtcOffset": "-05:00",
                    "TimeZoneId": "America/New_York"
                  }
                }
              }
            }""";

        AirportResponse response = gson.fromJson(json, AirportResponse.class);

        assertNotNull(response);
        assertNotNull(response.getAirportResource());
        assertNotNull(response.getAirportResource().getAirports());
        assertNotNull(response.getAirportResource().getAirports().getAirport());

        AirportResponse.Airport airport = response.getAirportResource().getAirports().getAirport();
        assertEquals("JFK", airport.getAirportCode());
        assertEquals("NYC", airport.getCityCode());
        assertEquals("US", airport.getCountryCode());
        assertEquals("Airport", airport.getLocationType());
        assertEquals("-05:00", airport.getUtcOffset());
        assertEquals("America/New_York", airport.getTimeZoneId());
        assertNotNull(airport.getPosition());
        assertNotNull(airport.getPosition().getCoordinate());
        assertEquals(BigDecimal.valueOf(40.6413), airport.getPosition().getCoordinate().getLatitude());
        assertEquals(BigDecimal.valueOf(-73.7781), airport.getPosition().getCoordinate().getLongitude());
        assertNotNull(airport.getNames());
        assertNotNull(airport.getNames().getName());
        assertEquals("en-GB", airport.getNames().getName().getLanguageCode());
        assertEquals("John F. Kennedy International Airport", airport.getNames().getName().getValue());
    }

    @Test
    public void testDeserialization_missingFields() {
        String json = """
            {
              "AirportResource": {
                "Airports": {
                  "Airport": {
                    "AirportCode": "JFK",
                    "Position": {
                      "Coordinate": {
                        "Latitude": 40.6413,
                        "Longitude": -73.7781
                      }
                    }
                  }
                }
              }
            }""";

        AirportResponse response = gson.fromJson(json, AirportResponse.class);

        assertNotNull(response);
        assertNotNull(response.getAirportResource());
        assertNotNull(response.getAirportResource().getAirports());
        assertNotNull(response.getAirportResource().getAirports().getAirport());

        AirportResponse.Airport airport = response.getAirportResource().getAirports().getAirport();
        assertEquals("JFK", airport.getAirportCode());
        assertNull(airport.getCityCode());
        assertNull(airport.getCountryCode());
        assertNull(airport.getLocationType());
        assertNull(airport.getUtcOffset());
        assertNull(airport.getTimeZoneId());
        assertNotNull(airport.getPosition());
        assertNotNull(airport.getPosition().getCoordinate());
        assertEquals(BigDecimal.valueOf(40.6413), airport.getPosition().getCoordinate().getLatitude());
        assertEquals(BigDecimal.valueOf(-73.7781), airport.getPosition().getCoordinate().getLongitude());
        assertNull(airport.getNames());
    }
}

