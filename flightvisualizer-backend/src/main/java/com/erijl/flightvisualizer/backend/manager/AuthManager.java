package com.erijl.flightvisualizer.backend.manager;

import com.erijl.flightvisualizer.backend.dto.AccessToken;
import com.erijl.flightvisualizer.backend.util.RestUtil;
import com.erijl.flightvisualizer.backend.util.UrlBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class AuthManager {

    private final RestUtil restUtil;
    private final Gson gson = new GsonBuilder().create();

    private String accessToken;
    private Date expirationDate;

    @Value("${flight.visualizer.api.url}")
    private String baseUrl;

    @Value("${flight.visualizer.api.client.id}")
    private String clientId;

    @Value("${flight.visualizer.api.client.secret}")
    private String clientSecret;

    public AuthManager(RestUtil restUtil) {
        this.restUtil = restUtil;
    }

    public String getBearerAccessToken() {
        if (accessToken == null) {
            AccessToken accessTokenDto = this.postAccessToken();
            if (accessTokenDto != null) {
                this.accessToken = accessTokenDto.getAccessToken();
                this.expirationDate = Date.from(
                        new Date().
                                toInstant().
                                plus(accessTokenDto.getExpiresIn(), ChronoUnit.MILLIS)
                );
            } else {
                //TODO add proper error handling
                return null;
            }
        }

        return String.format("Bearer %s", this.accessToken);
    }

    private AccessToken postAccessToken() {
        final String requestUrl = new UrlBuilder(this.baseUrl).accessToken().getUrl();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", this.clientId);
        body.add("client_secret", this.clientSecret);
        body.add("grant_type", "client_credentials");

        ResponseEntity<String> response = this.restUtil.exchangeRequest(
                requestUrl, HttpMethod.POST, this.restUtil.getStandardHeadersWithBody(), body
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return this.gson.fromJson(response.getBody(), AccessToken.class);
        } else {
            //TODO add proper error handling
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
        }

        return null;
    }
}
