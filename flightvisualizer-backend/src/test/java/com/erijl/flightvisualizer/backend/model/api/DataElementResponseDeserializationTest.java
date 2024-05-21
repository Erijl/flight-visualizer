package com.erijl.flightvisualizer.backend.model.api;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataElementResponseDeserializationTest {

    private final Gson gson = new Gson();

    @Test
    public void testDeserialization_validJson() {
        String json = """
            {
              "id": 123,
              "startLegSequenceNumber": 1,
              "endLegSequenceNumber": 2,
              "value": "SomeData"
            }""";

        DataElementResponse response = gson.fromJson(json, DataElementResponse.class);

        assertNotNull(response);
        assertEquals(123, response.getId());
        assertEquals(1, response.getStartLegSequenceNumber());
        assertEquals(2, response.getEndLegSequenceNumber());
        assertEquals("SomeData", response.getValue());
    }

    @Test
    public void testDeserialization_missingFields() {
        String json = """
            {
              "id": 123,
              "startLegSequenceNumber": 1,
              "endLegSequenceNumber": 2
            }""";

        DataElementResponse response = gson.fromJson(json, DataElementResponse.class);

        assertNotNull(response);
        assertEquals(123, response.getId());
        assertEquals(1, response.getStartLegSequenceNumber());
        assertEquals(2, response.getEndLegSequenceNumber());
        assertNull(response.getValue());
    }

    @Test
    public void testDeserialization_invalidJson() {
        String json = """
            {
              "id": 123,
              "startLegSequenceNumber": 1,
              "endLegSequenceNumber": 2,
              "value": "SomeData" """;

        assertThrows(com.google.gson.JsonSyntaxException.class, () -> gson.fromJson(json, DataElementResponse.class));
    }
}
