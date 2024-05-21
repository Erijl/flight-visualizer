package com.erijl.flightvisualizer.backend.model.api;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FlightScheduleResponseDeserializationTest {

    private final Gson gson = new Gson();

    @Test
    public void testDeserialization_validJson() {
        String json = """
            {
              "airline": "LH",
              "flightNumber": 1234,
              "suffix": "A",
              "periodOfOperationUTC": {
              },
              "periodOfOperationLT": {
              },
              "legs": [
                {
                }
              ],
              "dataElements": [
                {
                }
              ]
            }
            """;

        FlightScheduleResponse response = gson.fromJson(json, FlightScheduleResponse.class);

        assertNotNull(response);
        assertEquals("LH", response.getAirline());
        assertEquals(1234, response.getFlightNumber());
        assertEquals("A", response.getSuffix());
        assertNotNull(response.getPeriodOfOperationResponseUTC());
        assertNotNull(response.getPeriodOfOperationResponseLT());
        assertNotNull(response.getLegResponses());
        assertNotNull(response.getDataElementResponses());
    }

    @Test
    public void testDeserialization_missingFields() {
        String json = """
            {
              "airline": "LH",
              "flightNumber": 1234
            }
            """;

        FlightScheduleResponse response = gson.fromJson(json, FlightScheduleResponse.class);

        assertNotNull(response);
        assertEquals("LH", response.getAirline());
        assertEquals(1234, response.getFlightNumber());
        assertNull(response.getSuffix());
        assertNull(response.getPeriodOfOperationResponseUTC());
        assertNull(response.getPeriodOfOperationResponseLT());
        assertNull(response.getLegResponses());
        assertNull(response.getDataElementResponses());
    }

    @Test
    public void testDeserialization_invalidJson() {
        String json = """
            {
              "airline": "LH"
              "flightNumber": 1234
            }
            """;

        assertThrows(com.google.gson.JsonSyntaxException.class, () -> gson.fromJson(json, FlightScheduleResponse.class));
    }
}

