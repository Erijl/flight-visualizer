package com.erijl.flightvisualizer.backend.util;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
public class RestUtil {

    private final RestTemplate restTemplate;

    public RestUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public HttpHeaders getStandardHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    public HttpHeaders getStandardHeadersWithBody() {
        HttpHeaders headers = this.getStandardHeaders();
        headers.replace("Content-Type", Collections.singletonList("application/x-www-form-urlencoded"));

        return headers;
    }

    public HttpHeaders getStandardHeaders(String accessToken) {
        HttpHeaders headers = this.getStandardHeaders();
        headers.set("Authorization", accessToken);
        return headers;
    }

    public HttpEntity<String> getStandardHttpEntity() {
        return new HttpEntity<>(getStandardHeaders());
    }

    public ResponseEntity<String> exchangeRequest(String requestUrl, HttpMethod httpMethod, HttpHeaders httpHeaders) {
        return restTemplate.exchange(
                requestUrl,
                httpMethod,
                new HttpEntity<>(httpHeaders),
                String.class
        );
    }

    public ResponseEntity<String> exchangeRequest(String requestUrl, HttpMethod httpMethod, HttpHeaders httpHeaders,
                                                   MultiValueMap<String, String> body) {
        return restTemplate.exchange(
                requestUrl,
                httpMethod,
                new HttpEntity<>(body, httpHeaders),
                String.class
        );
    }
}
