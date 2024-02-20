package com.erijl.flightvisualizer.backend.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestUtilTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RestUtil restUtil;

    @Test
    public void testGetStandardHeaders() {
        HttpHeaders headers = restUtil.getStandardHeaders();
        assertEquals(MediaType.APPLICATION_JSON_VALUE, Objects.requireNonNull(headers.get("Accept")).getFirst());
        assertEquals("application/json", Objects.requireNonNull(headers.get("Content-Type")).getFirst());
    }

    @Test
    public void testGetStandardHeadersWithBody() {
        HttpHeaders headers = restUtil.getStandardHeadersWithBody();
        assertEquals(MediaType.APPLICATION_JSON_VALUE, Objects.requireNonNull(headers.get("Accept")).getFirst());
        assertEquals("application/x-www-form-urlencoded", Objects.requireNonNull(headers.get("Content-Type")).getFirst());
    }

    @Test
    public void testGetStandardHeadersWithAccessToken() {
        String accessToken = "Bearer token";
        HttpHeaders headers = restUtil.getStandardHeaders(accessToken);
        assertEquals(MediaType.APPLICATION_JSON_VALUE, Objects.requireNonNull(headers.get("Accept")).getFirst());
        assertEquals("application/json", Objects.requireNonNull(headers.get("Content-Type")).getFirst());
        assertEquals(accessToken, Objects.requireNonNull(headers.get("Authorization")).getFirst());
    }

    @Test
    public void testGetStandardHttpEntity() {
        HttpEntity<String> entity = restUtil.getStandardHttpEntity();
        assertEquals(MediaType.APPLICATION_JSON_VALUE, Objects.requireNonNull(entity.getHeaders().get("Accept")).getFirst());
        assertEquals("application/json", Objects.requireNonNull(entity.getHeaders().get("Content-Type")).getFirst());
    }

    @Test
    void exchangeRequestTest() {
        String requestUrl = "https://example.com/api";
        HttpMethod httpMethod = HttpMethod.GET;
        HttpHeaders httpHeaders = new HttpHeaders();
        String responseBody = "Response Body";
        ResponseEntity<String> mockResponseEntity = ResponseEntity.ok(responseBody);

        when(restTemplate.exchange(eq(requestUrl), eq(httpMethod), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponseEntity);

        ResponseEntity<String> responseEntity = restUtil.exchangeRequest(requestUrl, httpMethod, httpHeaders);

        assertEquals(mockResponseEntity, responseEntity);

        verify(restTemplate, times(1))
                .exchange(eq(requestUrl), eq(httpMethod), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testExchangeRequestWithBody() {
        String requestUrl = "https://example.com/api";
        HttpMethod httpMethod = HttpMethod.POST;
        HttpHeaders httpHeaders = new HttpHeaders();
        MultiValueMap<String, String> body = null;
        String responseBody = "Response Body";
        ResponseEntity<String> mockResponseEntity = ResponseEntity.ok(responseBody);

        when(restTemplate.exchange(eq(requestUrl), eq(httpMethod), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponseEntity);

        ResponseEntity<String> responseEntity = restUtil.exchangeRequest(requestUrl, httpMethod, httpHeaders, body);

        assertEquals(mockResponseEntity, responseEntity);

        verify(restTemplate, times(1))
                .exchange(eq(requestUrl), eq(httpMethod), any(HttpEntity.class), eq(String.class));
    }
}