package com.erijl.flightvisualizer.backend.manager;

import com.erijl.flightvisualizer.backend.dto.AccessToken;
import com.erijl.flightvisualizer.backend.util.RestUtil;
import com.erijl.flightvisualizer.backend.util.UrlBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class AuthManager {

    private final RestUtil restUtil;
    private final Gson gson = new GsonBuilder().create();

    private String accessToken;
    private Date expirationDate;

    @Value("${flight.visualizer.api.url")
    private String baseUrl;

    @Value("${flight.visualizer.api.client.id")
    private String clientId;

    @Value("${flight.visualizer.api.client.secret")
    private String clientSecret;

    public AuthManager(RestUtil restUtil) {
        this.restUtil = restUtil;
    }

    public String getBearerAccessToken() {
        if (accessToken == null || new Date().after(this.expirationDate)) {
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

        RestTemplate restTemplate = new RestTemplate();

        final String requestUrl = new UrlBuilder(this.baseUrl).accessToken().getUrl();

        ResponseEntity<String> response = restTemplate.exchange(
                requestUrl, HttpMethod.POST, this.restUtil.getStandardHttpEntity(), String.class);

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
