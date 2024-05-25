package com.erijl.flightvisualizer.backend.model.api;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AirlineResponseDeserializationTest {

    private final Gson gson = new Gson();

    @Test
    public void testDeserialization_validJson() {
        String json = """
            {
              "AirlineResource": {
                "Airlines": {
                  "Airline": {
                    "AirlineID": "LH",
                    "AirlineID_ICAO": "DLH",
                    "Names": {
                      "Name": {
                        "@LanguageCode": "en-GB",
                        "$": "Lufthansa"
                      }
                    }
                  }
                }
              }
            }""";

        AirlineResponse response = gson.fromJson(json, AirlineResponse.class);

        assertNotNull(response);
        assertNotNull(response.getAirlineResource());
        assertNotNull(response.getAirlineResource().getAirlines());
        assertNotNull(response.getAirlineResource().getAirlines().getAirline());

        AirlineResponse.Airline airline = response.getAirlineResource().getAirlines().getAirline();
        assertEquals("LH", airline.getAirlineID());
        assertEquals("DLH", airline.getAirlineID_ICAO());
        assertNotNull(airline.getNames());
        assertNotNull(airline.getNames().getName());

        AirlineResponse.Name name = airline.getNames().getName();
        assertEquals("en-GB", name.getLanguageCode());
        assertEquals("Lufthansa", name.getValue());
    }

    @Test
    public void testDeserialization_missingFields() {
        String json = """
            {
              "AirlineResource": {
                "Airlines": {
                  "Airline": {
                    "AirlineID": "LH",
                    "Names": {
                      "Name": {
                        "@LanguageCode": "en-GB",
                        "$": "Lufthansa"
                      }
                    }
                  }
                }
              }
            }""";

        AirlineResponse response = gson.fromJson(json, AirlineResponse.class);

        assertNotNull(response);
        assertNotNull(response.getAirlineResource());
        assertNotNull(response.getAirlineResource().getAirlines());
        assertNotNull(response.getAirlineResource().getAirlines().getAirline());

        assertEquals("LH", response.getAirlineResource().getAirlines().getAirline().getAirlineID());
        assertNull(response.getAirlineResource().getAirlines().getAirline().getAirlineID_ICAO());
    }

}

