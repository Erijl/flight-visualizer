package com.erijl.flightvisualizer.backend.model.api;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LegResponseDeserializationTest {

    private final Gson gson = new Gson();

    @Test
    public void testDeserialization_validJson() {
        String json = """
            {
              "sequenceNumber": 1,
              "origin": "JFK",
              "destination": "LHR",
              "serviceType": "J",
              "aircraftOwner": "LH",
              "aircraftType": "744",
              "aircraftConfigurationVersion": "P",
              "registration": "D-ABVH",
              "op": true,
              "aircraftDepartureTimeUTC": 1672531200,
              "aircraftDepartureTimeDateDiffUTC": 0,
              "aircraftDepartureTimeLT": 1672531200,
              "aircraftDepartureTimeDateDiffLT": 0,
              "aircraftDepartureTimeVariation": 0,
              "aircraftArrivalTimeUTC": 1672552800,
              "aircraftArrivalTimeDateDiffUTC": 0,
              "aircraftArrivalTimeLT": 1672552800,
              "aircraftArrivalTimeDateDiffLT": 0,
              "aircraftArrivalTimeVariation": 0
            }
            """;

        LegResponse response = gson.fromJson(json, LegResponse.class);

        assertNotNull(response);
        assertEquals(1, response.getSequenceNumber());
        assertEquals("JFK", response.getOrigin());
        assertEquals("LHR", response.getDestination());
        assertEquals("J", response.getServiceType());
        assertEquals("LH", response.getAircraftOwner());
        assertEquals("744", response.getAircraftType());
        assertEquals("P", response.getAircraftConfigurationVersion());
        assertEquals("D-ABVH", response.getRegistration());
        assertTrue(response.isOp());
        assertEquals(1672531200, response.getAircraftDepartureTimeUTC());
    }

    @Test
    public void testDeserialization_missingFields() {
        String json = """
            {
              "sequenceNumber": 1,
              "origin": "JFK",
              "destination": "LHR",
              "serviceType": "J",
              "aircraftOwner": "LH",
              "aircraftType": "744",
              "aircraftConfigurationVersion": "P"
            }
            """;

        LegResponse response = gson.fromJson(json, LegResponse.class);

        assertNotNull(response);
        assertEquals(1, response.getSequenceNumber());
        assertEquals("JFK", response.getOrigin());
        assertNull(response.getRegistration());
        assertFalse(response.isOp());
    }

    @Test
    public void testDeserialization_invalidJson() {
        String json = """
            {
              "sequenceNumber": 1,
              "origin": "JFK",
              "destination": "LHR"
            """;

        assertThrows(com.google.gson.JsonSyntaxException.class, () -> gson.fromJson(json, LegResponse.class));
    }
}
