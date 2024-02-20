package com.erijl.flightvisualizer.backend.config;

import com.erijl.flightvisualizer.backend.manager.AuthManager;
import com.erijl.flightvisualizer.backend.dto.FlightScheduleResponse;
import com.erijl.flightvisualizer.backend.model.*;
import com.erijl.flightvisualizer.backend.repository.*;
import com.erijl.flightvisualizer.backend.service.AircraftService;
import com.erijl.flightvisualizer.backend.service.AirlineService;
import com.erijl.flightvisualizer.backend.service.AirportService;
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
    private final AuthManager authManager;

    private final AirlineService airlineService;
    private final AircraftService aircraftService;
    private final AirportService airportService;

    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final AircraftRepository aircraftRepository;

    private final FlightScheduleRepository flightScheduleRepository;
    private final FlightScheduleOperationPeriodRepository flightScheduleOperationPeriodRepository;
    private final FlightScheduleDataElementRepository flightScheduleDataElementRepository;
    private final FlightScheduleLegRepository flightScheduleLegRepository;
    private final Gson gson = new GsonBuilder().create();


    public CronScheduler(CustomTimeUtil customTimeUtil, RestUtil restUtil, AirlineService airlineService, AircraftService aircraftService, AirportService airportService, AirportRepository airportRepository, AircraftRepository aircraftRepository, FlightScheduleRepository flightScheduleRepository, AuthManager authManager, AirlineRepository airlineRepository, FlightScheduleOperationPeriodRepository flightScheduleOperationPeriodRepository, FlightScheduleDataElementRepository flightScheduleDataElementRepository, FlightScheduleLegRepository flightScheduleLegRepository) {
        this.customTimeUtil = customTimeUtil;
        this.restUtil = restUtil;
        this.airlineService = airlineService;
        this.aircraftService = aircraftService;
        this.airportService = airportService;
        this.airportRepository = airportRepository;
        this.aircraftRepository = aircraftRepository;
        this.flightScheduleRepository = flightScheduleRepository;
        this.authManager = authManager;
        this.airlineRepository = airlineRepository;
        this.flightScheduleOperationPeriodRepository = flightScheduleOperationPeriodRepository;
        this.flightScheduleDataElementRepository = flightScheduleDataElementRepository;
        this.flightScheduleLegRepository = flightScheduleLegRepository;
    }

    @Scheduled(initialDelay = 1000)
    public void fetchTodaysFLightSchedule() {
        //TODO add chron scheduler log
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
            //this.insertFlightScheduleResponse(aggregatedFlights);
        }
    }

    private void insertFlightScheduleResponse(List<FlightScheduleResponse> flightScheduleResponseList) {
        this.ensureForeignKeyRelation(flightScheduleResponseList);

        flightScheduleResponseList.forEach(flightScheduleResponse -> {
            FlightScheduleOperationPeriod operationPeriod = this.flightScheduleOperationPeriodRepository.save(
                    new FlightScheduleOperationPeriod(
                            this.customTimeUtil.convertDDMMMYYToSQLDate(flightScheduleResponse.getPeriodOfOperationResponseUTC().getStartDate()),
                            this.customTimeUtil.convertDDMMMYYToSQLDate(flightScheduleResponse.getPeriodOfOperationResponseUTC().getEndDate()),
                            flightScheduleResponse.getPeriodOfOperationResponseUTC().getDaysOfOperation(),
                            this.customTimeUtil.convertDDMMMYYToSQLDate(flightScheduleResponse.getPeriodOfOperationResponseLT().getStartDate()),
                            this.customTimeUtil.convertDDMMMYYToSQLDate(flightScheduleResponse.getPeriodOfOperationResponseLT().getEndDate()),
                            flightScheduleResponse.getPeriodOfOperationResponseLT().getDaysOfOperation()
                    )
            );

            String airlineCode = flightScheduleResponse.getAirline();
            Airline airline = (airlineCode == null || airlineCode.isEmpty()) ? null : this.airlineRepository.findById(airlineCode).orElse(null);

            FlightSchedule flightSchedule = this.flightScheduleRepository.save(
                    new FlightSchedule(
                        airline,
                        operationPeriod,
                        flightScheduleResponse.getFlightNumber(),
                        flightScheduleResponse.getSuffix()
                    )
            );

            flightScheduleResponse.getDataElementResponses().forEach(flightScheduleDataElement -> {
                this.flightScheduleDataElementRepository.save(
                        new FlightScheduleDataElement(
                                flightSchedule,
                                flightScheduleDataElement.getStartLegSequenceNumber(),
                                flightScheduleDataElement.getEndLegSequenceNumber(),
                                flightScheduleDataElement.getId(),
                                flightScheduleDataElement.getValue()
                        )
                );
            });

            flightScheduleResponse.getLegResponses().forEach(flightScheduleLeg -> {
                String origin = flightScheduleLeg.getOrigin();
                String destination = flightScheduleLeg.getDestination();
                String aircraftOwner = flightScheduleLeg.getAircraftOwner();
                String aircraftType = flightScheduleLeg.getAircraftType();

                Airport originAirport = (origin == null || origin.isEmpty()) ? null : this.airportRepository.findById(origin).orElse(null);
                Airport destinationAirport = (destination == null || destination.isEmpty()) ? null : this.airportRepository.findById(destination).orElse(null);
                Airline aircraftOwnerAirline = (aircraftOwner == null || aircraftOwner.isEmpty()) ? null : this.airlineRepository.findById(aircraftOwner).orElse(null);
                Aircraft aircraft = (aircraftType == null || aircraftType.isEmpty()) ? null : this.aircraftRepository.findById(aircraftType).orElse(null);

                this.flightScheduleLegRepository.save(
                        new FlightScheduleLeg(
                                flightSchedule,
                                flightScheduleLeg.getSequenceNumber(),
                                originAirport,
                                destinationAirport,
                                flightScheduleLeg.getServiceType(),
                                aircraftOwnerAirline,
                                aircraft,
                                flightScheduleLeg.getAircraftConfigurationVersion(),
                                flightScheduleLeg.getRegistration(),
                                flightScheduleLeg.isOp(),
                                flightScheduleLeg.getAircraftDepartureTimeUTC(),
                                flightScheduleLeg.getAircraftDepartureTimeDateDiffUTC(),
                                flightScheduleLeg.getAircraftDepartureTimeLT(),
                                flightScheduleLeg.getAircraftDepartureTimeDateDiffLT(),
                                flightScheduleLeg.getAircraftDepartureTimeVariation(),
                                flightScheduleLeg.getAircraftArrivalTimeUTC(),
                                flightScheduleLeg.getAircraftArrivalTimeDateDiffUTC(),
                                flightScheduleLeg.getAircraftArrivalTimeLT(),
                                flightScheduleLeg.getAircraftArrivalTimeDateDiffLT(),
                                flightScheduleLeg.getAircraftArrivalTimeVariation()
                        )
                );
            });

        });
    }

    private void ensureForeignKeyRelation(List<FlightScheduleResponse> flightScheduleResponseList) {
        HashSet<String> iataAirlineCodes = new HashSet<>();
        HashSet<String> iataAirportCodes = new HashSet<>();
        HashSet<String> iataAircraftCodes = new HashSet<>();

        flightScheduleResponseList.forEach(flightScheduleResponse -> {

            if(flightScheduleResponse.getAirline() != null && flightScheduleResponse.getAirline().length() <= 2) {
                iataAirlineCodes.add(flightScheduleResponse.getAirline());
            }

            flightScheduleResponse.getLegResponses().forEach(flightScheduleLeg -> {
                iataAircraftCodes.add(flightScheduleLeg.getAircraftType());
                iataAirportCodes.add(flightScheduleLeg.getOrigin());
                iataAirportCodes.add(flightScheduleLeg.getDestination());

                if(flightScheduleLeg.getAircraftOwner() != null && flightScheduleLeg.getAircraftOwner().length() <= 2) {
                    iataAirlineCodes.add(flightScheduleLeg.getAircraftOwner());
                }
            });
        });

        iataAircraftCodes.remove(null);
        iataAirportCodes.remove(null);
        iataAirportCodes.remove(null);
        iataAirlineCodes.remove(null);

        System.out.println("Airports: " + iataAirportCodes.size());
        System.out.println("Aircrafts: " + iataAircraftCodes.size());
        System.out.println("Airlines: " + iataAirlineCodes.size());

        iataAirlineCodes.forEach(airlineCode -> {
            System.out.println(airlineCode);
            this.airlineService.ensureAirlineExists(airlineCode);
        });

        iataAircraftCodes.forEach(aircraftCode -> {
            System.out.println(aircraftCode);
            this.aircraftService.ensureAircraftExists(aircraftCode);
        });

        iataAirportCodes.forEach(airportCode -> {
            System.out.println(airportCode);
            this.airportService.ensureAirportExists(airportCode);
        });
    }
}
