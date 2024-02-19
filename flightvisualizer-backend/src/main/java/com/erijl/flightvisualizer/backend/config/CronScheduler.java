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

            Optional<Airline> airline = this.airlineRepository.findById(flightScheduleResponse.getAirline());
            if (airline.isEmpty()) return;

            FlightSchedule flightSchedule = this.flightScheduleRepository.save(
                    new FlightSchedule(
                        airline.get(),
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
                Optional<Airport> originAirport = this.airportRepository.findById(flightScheduleLeg.getOrigin());
                Optional<Airport> destinationAirport = this.airportRepository.findById(flightScheduleLeg.getDestination());
                Optional<Airline> aircraftOwnerAirline = this.airlineRepository.findById(flightScheduleLeg.getAircraftOwner());
                Optional<Aircraft> aircraft = this.aircraftRepository.findById(flightScheduleLeg.getAircraftType());

                if (originAirport.isEmpty() || destinationAirport.isEmpty() || aircraftOwnerAirline.isEmpty() || aircraft.isEmpty()) return;
                this.flightScheduleLegRepository.save(
                        new FlightScheduleLeg(
                                flightSchedule,
                                flightScheduleLeg.getSequenceNumber(),
                                originAirport.get(),
                                destinationAirport.get(),
                                flightScheduleLeg.getServiceType(),
                                aircraftOwnerAirline.get(),
                                aircraft.get(),
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
            iataAirlineCodes.add(flightScheduleResponse.getAirline());

            flightScheduleResponse.getLegResponses().forEach(flightScheduleLeg -> {
                iataAircraftCodes.add(flightScheduleLeg.getAircraftType());
                iataAirportCodes.add(flightScheduleLeg.getOrigin());
                iataAirportCodes.add(flightScheduleLeg.getDestination());
                iataAirlineCodes.add(flightScheduleLeg.getAircraftOwner());
            });
        });

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
