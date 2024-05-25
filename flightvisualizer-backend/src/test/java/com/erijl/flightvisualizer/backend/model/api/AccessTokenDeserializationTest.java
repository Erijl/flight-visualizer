package com.erijl.flightvisualizer.backend.model.api;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccessTokenDeserializationTest {

    private final Gson gson = new Gson();

    @Test
    public void testDeserialization_validJson() {
        String json = "{\"access_token\":\"your_access_token\",\"token_type\":\"bearer\",\"expires_in\":3600}";

        AccessToken accessToken = gson.fromJson(json, AccessToken.class);

        assertNotNull(accessToken);
        assertEquals("your_access_token", accessToken.getAccessToken());
        assertEquals("bearer", accessToken.getTokenType());
        assertEquals(3600, accessToken.getExpiresIn());
    }

    @Test
    public void testDeserialization_missingFields() {
        String json = "{\"access_token\":\"your_access_token\",\"token_type\":\"bearer\"}";

        AccessToken accessToken = gson.fromJson(json, AccessToken.class);

        assertNotNull(accessToken);
        assertEquals("your_access_token", accessToken.getAccessToken());
        assertEquals("bearer", accessToken.getTokenType());
        assertEquals(0, accessToken.getExpiresIn()); // should default to 0
    }

    @Test
    public void testDeserialization_invalidJson() {
        String json = "{\"access_token\":\"your_access_token\",\"token_type\":\"bearer\",\"expires_in\":3600";

        assertThrows(com.google.gson.JsonSyntaxException.class, () -> gson.fromJson(json, AccessToken.class));
    }
}