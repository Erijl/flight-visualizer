package com.erijl.flightvisualizer.backend.model.api;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AircraftResponseDeserializationTest {

    private final Gson gson = new Gson();

    @Test
    public void testDeserialization_validJson() {
        String json = """
                {
                  "AircraftResource": {
                    "AircraftSummaries": {
                      "AircraftSummary": {
                        "AircraftCode": "73H",
                        "Names": {
                          "Name": {
                            "@LanguageCode": "en-GB",
                            "$": "Boeing 737-800 Passenger"
                          }
                        },
                        "AirlineEquipCode": "73H"
                      }
                    }
                  }
                }""";

        AircraftResponse response = gson.fromJson(json, AircraftResponse.class);

        assertNotNull(response);
        assertNotNull(response.getAircraftResource());
        assertNotNull(response.getAircraftResource().getAircraftSummaries());
        assertNotNull(response.getAircraftResource().getAircraftSummaries().getAircraftSummary());

        AircraftResponse.AircraftSummary summary = response.getAircraftResource().getAircraftSummaries().getAircraftSummary();
        assertEquals("73H", summary.getAircraftCode());
        assertEquals("73H", summary.getAirlineEquipCode());
        assertNotNull(summary.getNames());
        assertNotNull(summary.getNames().getName());

        AircraftResponse.Name name = summary.getNames().getName();
        assertEquals("en-GB", name.getLanguageCode());
        assertEquals("Boeing 737-800 Passenger", name.getValue());
    }

    @Test
    public void testDeserialization_missingFields() {
        String json = "{\"AircraftResource\": {}}";
        AircraftResponse response = gson.fromJson(json, AircraftResponse.class);
        assertNotNull(response);
        assertNull(response.getAircraftResource().getAircraftSummaries());
    }

    @Test
    public void testDeserialization_invalidJson() {
        String json = "{ invalid json }";
        assertThrows(JsonSyntaxException.class, () -> gson.fromJson(json, AircraftResponse.class));
    }
}
