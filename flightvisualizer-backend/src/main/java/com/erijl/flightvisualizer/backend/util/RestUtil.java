package com.erijl.flightvisualizer.backend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class RestUtil {

    public HttpHeaders getStandardHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    public HttpEntity<String> getStandardHttpEntity() {
        return new HttpEntity<>(getStandardHeaders());
    }
}
