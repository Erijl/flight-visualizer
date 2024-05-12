package com.erijl.flightvisualizer.backend.config;

import com.erijl.flightvisualizer.backend.manager.AuthManager;
import com.erijl.flightvisualizer.backend.model.api.FlightScheduleResponse;
import com.erijl.flightvisualizer.backend.model.entities.*;
import com.erijl.flightvisualizer.backend.model.internal.CoordinatePair;
import com.erijl.flightvisualizer.backend.model.repository.*;
import com.erijl.flightvisualizer.backend.service.AircraftService;
import com.erijl.flightvisualizer.backend.service.AirlineService;
import com.erijl.flightvisualizer.backend.service.AirportService;
import com.erijl.flightvisualizer.backend.util.CustomTimeUtil;
import com.erijl.flightvisualizer.backend.util.MathUtil;
import com.erijl.flightvisualizer.backend.util.RestUtil;
import com.erijl.flightvisualizer.backend.util.UrlBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
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

    private final FlightScheduleCronRunRepository flightScheduleCronRunRepository;

    private final FlightScheduleRepository flightScheduleRepository;
    private final FlightScheduleOperationPeriodRepository flightScheduleOperationPeriodRepository;
    private final FlightScheduleDataElementRepository flightScheduleDataElementRepository;
    private final FlightScheduleLegRepository flightScheduleLegRepository;
    private final Gson gson = new GsonBuilder().create();


    //TODO remove java.util.date globally
    public CronScheduler(CustomTimeUtil customTimeUtil, RestUtil restUtil, AirlineService airlineService, AircraftService aircraftService, AirportService airportService, FlightScheduleRepository flightScheduleRepository, AuthManager authManager, FlightScheduleCronRunRepository flightScheduleCronRunRepository, FlightScheduleOperationPeriodRepository flightScheduleOperationPeriodRepository, FlightScheduleDataElementRepository flightScheduleDataElementRepository, FlightScheduleLegRepository flightScheduleLegRepository) {
        this.customTimeUtil = customTimeUtil;
        this.restUtil = restUtil;
        this.airlineService = airlineService;
        this.aircraftService = aircraftService;
        this.airportService = airportService;
        this.flightScheduleRepository = flightScheduleRepository;
        this.authManager = authManager;
        this.flightScheduleCronRunRepository = flightScheduleCronRunRepository;
        this.flightScheduleOperationPeriodRepository = flightScheduleOperationPeriodRepository;
        this.flightScheduleDataElementRepository = flightScheduleDataElementRepository;
        this.flightScheduleLegRepository = flightScheduleLegRepository;
    }

    //@Scheduled(fixedRate = 1000 * 60 * 60)
    public void fetchTodaysFlightSchedule() {
        String currentLocalDateString = LocalDate.now(ZoneId.of("UTC")).toString();
        FlightScheduleCronRun possibleCronRuns = this.flightScheduleCronRunRepository.
                findFlightScheduleCronRunByCronRunDateUtcEquals(currentLocalDateString);

        if (possibleCronRuns != null && possibleCronRuns.getCronRunDateUtc().equals(currentLocalDateString)) {
            System.out.printf("Cron Run Already Exists for %s%n", currentLocalDateString);
            return;
        }

        FlightScheduleCronRun cronRun = new FlightScheduleCronRun();
        LocalDate currentDateUtc = LocalDate.now(ZoneId.of("UTC"));
        cronRun.setCronRunDateUtc(currentDateUtc.toString());

        cronRun = this.flightScheduleCronRunRepository.save(cronRun);
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
            Type listType = new TypeToken<List<FlightScheduleResponse>>() {
            }.getType();
            aggregatedFlights = this.gson.fromJson(response.getBody(), listType);
        } else {
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
        }

        if (aggregatedFlights != null && !aggregatedFlights.isEmpty()) {
            this.insertFlightScheduleResponse(aggregatedFlights, cronRun);
        }
    }

    private void insertFlightScheduleResponse(List<FlightScheduleResponse> flightScheduleResponseList, FlightScheduleCronRun cronRun) {
        this.ensureForeignKeyRelation(flightScheduleResponseList, cronRun);

        List<FlightScheduleDataElement> flightScheduleDataElements = new ArrayList<>();
        List<FlightScheduleLeg> flightScheduleLegs = new ArrayList<>();

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
            Airline airline = (airlineCode == null || airlineCode.isEmpty()) ? null : this.airlineService.getAirlineById(airlineCode);

            FlightSchedule flightSchedule = this.flightScheduleRepository.save(
                    new FlightSchedule(
                            airline,
                            operationPeriod,
                            flightScheduleResponse.getFlightNumber(),
                            flightScheduleResponse.getSuffix()
                    )
            );

            flightScheduleResponse.getDataElementResponses().forEach(flightScheduleDataElement ->
                    flightScheduleDataElements.add(
                            new FlightScheduleDataElement(
                                    flightSchedule,
                                    flightScheduleDataElement.getStartLegSequenceNumber(),
                                    flightScheduleDataElement.getEndLegSequenceNumber(),
                                    flightScheduleDataElement.getId(),
                                    flightScheduleDataElement.getValue()
                            )
                    ));

            flightScheduleResponse.getLegResponses().forEach(flightScheduleLeg -> {
                String origin = flightScheduleLeg.getOrigin();
                String destination = flightScheduleLeg.getDestination();
                String aircraftOwner = flightScheduleLeg.getAircraftOwner();
                String aircraftType = flightScheduleLeg.getAircraftType();

                Airport originAirport = (origin == null || origin.isEmpty()) ? null : this.airportService.getAirportById(origin);
                Airport destinationAirport = (destination == null || destination.isEmpty()) ? null : this.airportService.getAirportById(destination);
                Airline aircraftOwnerAirline = (aircraftOwner == null || aircraftOwner.isEmpty()) ? null : this.airlineService.getAirlineById(aircraftOwner);
                Aircraft aircraft = (aircraftType == null || aircraftType.isEmpty()) ? null : this.aircraftService.getAircraftById(aircraftType);

                CoordinatePair drawableCoordinates = MathUtil.calculateDrawableCoordinates(originAirport, destinationAirport); //TODO move to wrapper class

                flightScheduleLegs.add(
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
                                flightScheduleLeg.getAircraftArrivalTimeVariation(),
                                CustomTimeUtil.calculateDurationInMinutes(flightScheduleLeg),
                                MathUtil.calculateDistanceBetweenAirports(originAirport, destinationAirport),
                                drawableCoordinates.getOriginLongitude(),
                                drawableCoordinates.getDestinationLongitude()
                        )
                );
            });
        });
        this.flightScheduleDataElementRepository.saveAll(flightScheduleDataElements);
        this.flightScheduleLegRepository.saveAll(flightScheduleLegs);

        this.flightScheduleCronRunRepository.updateCronRunFinish(cronRun.getCronRunId(), new Timestamp(new Date().getTime()));
    }

    private void ensureForeignKeyRelation(List<FlightScheduleResponse> flightScheduleResponseList, FlightScheduleCronRun cronRun) {
        HashSet<String> iataAirlineCodes = new HashSet<>();
        HashSet<String> iataAirportCodes = new HashSet<>();
        HashSet<String> iataAircraftCodes = new HashSet<>();

        flightScheduleResponseList.forEach(flightScheduleResponse -> {

            if (flightScheduleResponse.getAirline() != null && flightScheduleResponse.getAirline().length() <= 2) {
                iataAirlineCodes.add(flightScheduleResponse.getAirline());
            }

            flightScheduleResponse.getLegResponses().forEach(flightScheduleLeg -> {
                iataAircraftCodes.add(flightScheduleLeg.getAircraftType());
                iataAirportCodes.add(flightScheduleLeg.getOrigin());
                iataAirportCodes.add(flightScheduleLeg.getDestination());

                if (flightScheduleLeg.getAircraftOwner() != null && flightScheduleLeg.getAircraftOwner().length() <= 2) {
                    iataAirlineCodes.add(flightScheduleLeg.getAircraftOwner());
                }
            });
        });

        iataAircraftCodes.remove(null);
        iataAirportCodes.remove(null);
        iataAirlineCodes.remove(null);

        cronRun.setAirlineCount(iataAirlineCodes.size());
        cronRun.setAircraftCount(iataAircraftCodes.size());
        cronRun.setAirportCount(iataAirportCodes.size());
        cronRun.setFlightScheduleCount(flightScheduleResponseList.size());
        this.flightScheduleCronRunRepository.save(cronRun);

        iataAirlineCodes.forEach(this.airlineService::ensureAirlineExists);
        iataAircraftCodes.forEach(this.aircraftService::ensureAircraftExists);
        iataAirportCodes.forEach(this.airportService::ensureAirportExists);
    }



}
