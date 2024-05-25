package com.erijl.flightvisualizer.backend.model.api;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PeriodOfOperationResponseDeserializationTest {

    private final Gson gson = new Gson();

    @Test
    public void testDeserialization_validJson() {
        String json = """
            {
              "startDate": "2023-12-25",
              "endDate": "2024-01-10",
              "daysOfOperation": "1234567"
            }
            """;

        PeriodOfOperationResponse response = gson.fromJson(json, PeriodOfOperationResponse.class);

        assertNotNull(response);
        assertEquals("2023-12-25", response.getStartDate());
        assertEquals("2024-01-10", response.getEndDate());
        assertEquals("1234567", response.getDaysOfOperation());
    }

    @Test
    public void testDeserialization_missingFields() {
        String json = """
            {
              "startDate": "2023-12-25",
              "endDate": "2024-01-10"
            }
            """;

        PeriodOfOperationResponse response = gson.fromJson(json, PeriodOfOperationResponse.class);

        assertNotNull(response);
        assertEquals("2023-12-25", response.getStartDate());
        assertEquals("2024-01-10", response.getEndDate());
        assertNull(response.getDaysOfOperation());
    }

    @Test
    public void testDeserialization_invalidJson() {
        String json = """
            {
              "startDate": "2023-12-25"
              "endDate": "2024-01-10"
              "daysOfOperation": "1234567"
            }
            """;

        assertThrows(com.google.gson.JsonSyntaxException.class, () -> gson.fromJson(json, PeriodOfOperationResponse.class));
    }
}
