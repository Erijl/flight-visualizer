package com.erijl.flightvisualizer.backend.config;

import com.erijl.flightvisualizer.backend.manager.AuthManager;
import com.erijl.flightvisualizer.backend.dto.FlightScheduleResponse;
import com.erijl.flightvisualizer.backend.model.WeekRepresentation;
import com.erijl.flightvisualizer.backend.service.AircraftService;
import com.erijl.flightvisualizer.backend.service.AirlineService;
import com.erijl.flightvisualizer.backend.util.CustomTimeUtil;
import com.erijl.flightvisualizer.backend.util.RestUtil;
import com.erijl.flightvisualizer.backend.util.UrlBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class CronScheduler {

    @Value("${flight.visualizer.api.url}")
    private String baseUrl;

    CustomTimeUtil customTimeUtil;
    private final RestUtil restUtil;
    private final AirlineService airlineService;
    private final AircraftService aircraftService;
    private final AuthManager authManager;
    private final Gson gson = new GsonBuilder().create();


    public CronScheduler(CustomTimeUtil customTimeUtil, RestUtil restUtil, AirlineService airlineService, AircraftService aircraftService, AuthManager authManager) {
        this.customTimeUtil = customTimeUtil;
        this.restUtil = restUtil;
        this.airlineService = airlineService;
        this.aircraftService = aircraftService;
        this.authManager = authManager;
    }

    @Scheduled(initialDelay = 1000)
    public void fetchTodaysFLightSchedule() {
        Date today = new Date();
        Date tomorrow = Date.from(
                new Date().
                        toInstant().
                        plus(1, ChronoUnit.DAYS)
        );

        String requestUrl = new UrlBuilder(this.baseUrl)
                .flightSchedule()
                .filterAirlineCodes("LH")
                .filterStartDate(this.customTimeUtil.convertDateToDDMMMYY(today))
                .filterEndDate(this.customTimeUtil.convertDateToDDMMMYY(tomorrow))
                .filterDaysOfOperation(new WeekRepresentation(today).toDaysOfOperationString())
                .getUrl();

        ResponseEntity<String> response = this.restUtil.exchangeRequest(
                requestUrl, HttpMethod.GET, this.restUtil.getStandardHeaders(this.authManager.getBearerAccessToken()));

        List<FlightScheduleResponse> aggregatedFlights = new ArrayList<>();
        if (response.getStatusCode() == HttpStatus.OK) {
            Type listType = new TypeToken<List<FlightScheduleResponse>>(){}.getType();
            aggregatedFlights = this.gson.fromJson(response.getBody(), listType);
        } else {
            //TODO add proper error handling, especially for 206 & 400
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
        }

        if(aggregatedFlights != null || !aggregatedFlights.isEmpty()) {
            this.insertFlightScheduleResponse(aggregatedFlights);
        }
    }

    private void insertFlightScheduleResponse(List<FlightScheduleResponse> flightScheduleResponseList) {
        this.ensureForeignKeyRelation(flightScheduleResponseList);
    }

    private void ensureForeignKeyRelation(List<FlightScheduleResponse> flightScheduleResponseList) {
        HashSet<String> iataAirlineCodes = new HashSet<>();
        HashSet<String> iataAirportCodes = new HashSet<>();
        HashSet<String> iataAircraftCodes = new HashSet<>();

        flightScheduleResponseList.forEach(flightScheduleResponse -> {
            iataAirlineCodes.add(flightScheduleResponse.getAirline());

            flightScheduleResponse.getLegResponses().forEach(flightScheduleLeg -> {
                iataAircraftCodes.add(flightScheduleLeg.getAircraftType());
                iataAirportCodes.add(flightScheduleLeg.getOrigin());
                iataAirportCodes.add(flightScheduleLeg.getDestination());
            });
        });

        System.out.println("Airports: " + iataAirportCodes.size());
        System.out.println("Aircrafts: " + iataAircraftCodes.size());

        iataAirlineCodes.forEach(airlineCode -> {
            System.out.println(airlineCode);
            this.airlineService.ensureAirlineExists(airlineCode);
        });

        iataAircraftCodes.forEach(aircraftCode -> {
            System.out.println(aircraftCode);
            this.aircraftService.ensureAircraftExists(aircraftCode);
        });
    }
}
