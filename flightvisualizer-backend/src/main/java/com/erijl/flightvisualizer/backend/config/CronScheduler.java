package com.erijl.flightvisualizer.backend.config;

import com.erijl.flightvisualizer.backend.manager.AuthManager;
import com.erijl.flightvisualizer.backend.model.api.FlightScheduleResponse;
import com.erijl.flightvisualizer.backend.model.api.LegResponse;
import com.erijl.flightvisualizer.backend.model.entities.*;
import com.erijl.flightvisualizer.backend.model.internal.CoordinatePair;
import com.erijl.flightvisualizer.backend.model.internal.FlightScheduleOperationPeriodKey;
import com.erijl.flightvisualizer.backend.model.internal.PerformanceTracker;
import com.erijl.flightvisualizer.backend.model.internal.WeekRepresentation;
import com.erijl.flightvisualizer.backend.model.repository.*;
import com.erijl.flightvisualizer.backend.service.AircraftService;
import com.erijl.flightvisualizer.backend.service.AirlineService;
import com.erijl.flightvisualizer.backend.service.AirportService;
import com.erijl.flightvisualizer.backend.util.TimeUtil;
import com.erijl.flightvisualizer.backend.util.MathUtil;
import com.erijl.flightvisualizer.backend.util.RestUtil;
import com.erijl.flightvisualizer.backend.builder.UrlBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class CronScheduler {

    @Value("${flight.visualizer.api.url}")
    private String baseUrl;

    TimeUtil timeUtil;
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
    private final PerformanceTracker performanceTracker;


    //TODO remove java.util.date globally
    //TODO make TimeUtil static
    public CronScheduler(TimeUtil timeUtil, RestUtil restUtil, AirlineService airlineService, AircraftService aircraftService, AirportService airportService, FlightScheduleRepository flightScheduleRepository, AuthManager authManager, FlightScheduleCronRunRepository flightScheduleCronRunRepository, FlightScheduleOperationPeriodRepository flightScheduleOperationPeriodRepository, FlightScheduleDataElementRepository flightScheduleDataElementRepository, FlightScheduleLegRepository flightScheduleLegRepository) {
        this.timeUtil = timeUtil;
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
        this.performanceTracker = new PerformanceTracker();
    }

    //@Scheduled(initialDelay = 1000)
    public void fetchOldFlightSchedules() {
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 10);
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            try {
                log.info("[{}]: Fetching flight schedule for {}", LocalDate.now(), currentDate);
                fetchFlightSchedule(currentDate);
            } catch (Exception e) {
                log.error("Error fetching flight schedule for {}", currentDate, e);
            }
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                log.warn("Delay interrupted", e);
            }
            currentDate = currentDate.plusDays(1);
        }
    }

    //@Scheduled(fixedRate = 1000 * 60 * 60)
    //public void fetchCurrentFlightSchedule() {
    //    LocalDate currentDate = LocalDate.now(ZoneId.of("UTC"));
//
    //    try {
    //        log.info("[{}]: Fetching flight schedule for {}", LocalDate.now(), currentDate);
    //        performanceTracker.startTracking();
//
    //        performanceTracker.addPerformance("Start fetching");
    //        fetchFlightSchedule(currentDate);
    //        performanceTracker.stop();
//
    //        log.info("Flight schedule for {} fetched", currentDate);
    //        log.info(performanceTracker.getPerformanceTrackrecordString());
    //    } catch (Exception e) {
    //        log.error("Error fetching flight schedule for {}", currentDate, e);
    //    }
    //}

    public void fetchFlightSchedule(LocalDate dateToFetch) {
        FlightScheduleCronRun possibleCronRuns = this.flightScheduleCronRunRepository.
                findFlightScheduleCronRunByCronRunDateUtcEquals(dateToFetch.toString());

        if (possibleCronRuns != null && possibleCronRuns.getCronRunDateUtc().equals(dateToFetch.toString())) {
            System.out.printf("Cron Run Already Exists for %s%n", dateToFetch);
            return;
        }

        FlightScheduleCronRun cronRun = new FlightScheduleCronRun();
        cronRun.setCronRunDateUtc(dateToFetch.toString());
        cronRun = this.flightScheduleCronRunRepository.save(cronRun);

        String requestUrl = new UrlBuilder(this.baseUrl)
                .flightSchedule()
                .filterAirlineCodes("LH")
                .filterStartDate(this.timeUtil.convertDateToDDMMMYY(dateToFetch))
                .filterEndDate(this.timeUtil.convertDateToDDMMMYY(dateToFetch.plus(1, ChronoUnit.DAYS)))
                .filterDaysOfOperation(new WeekRepresentation(dateToFetch).toDaysOfOperationString())
                .getUrl();

        performanceTracker.addPerformance("Requesting flight Schedule");
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
            performanceTracker.addPerformance("Inserting flight schedule");
            this.insertFlightScheduleResponse(aggregatedFlights, cronRun);
        }
    }

    private void insertFlightScheduleResponse(List<FlightScheduleResponse> flightScheduleResponseList, FlightScheduleCronRun cronRun) {
        List<FlightScheduleDataElement> flightScheduleDataElements = new ArrayList<>();
        List<FlightScheduleLeg> flightScheduleLegs = new ArrayList<>();

        Set<String> airlineCodes = flightScheduleResponseList.stream()
                .flatMap(flightScheduleResponse -> {
                    Set<String> airlineCodesForResponse = new HashSet<>();
                    airlineCodesForResponse.add(flightScheduleResponse.getAirline());

                    flightScheduleResponse.getLegResponses().stream()
                            .map(LegResponse::getAircraftOwner)
                            .forEach(airlineCodesForResponse::add);

                    return airlineCodesForResponse.stream();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<String> airportCodes = flightScheduleResponseList.stream()
                .flatMap(fsr -> fsr.getLegResponses().stream())
                .flatMap(lr -> Stream.of(lr.getOrigin(), lr.getDestination()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<String> aircraftCodes = flightScheduleResponseList.stream()
                .flatMap(fsr -> fsr.getLegResponses().stream())
                .map(LegResponse::getAircraftType)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        performanceTracker.addPerformance("Ensure foreign key relation");
        this.ensureForeignKeyRelationInBatch(flightScheduleResponseList, cronRun, airlineCodes, airportCodes, aircraftCodes);

        performanceTracker.addPerformance("Instantiating bulk objects");
        Map<String, FlightScheduleOperationPeriod> operationPeriodMap = new HashMap<>();

        Map<String, Airline> airlines = airlineService.getAirlinesById(airlineCodes);
        Map<String, Airport> airports = airportService.getAirportsById(airportCodes);
        Map<String, Aircraft> aircrafts = aircraftService.getAircraftsById(aircraftCodes);

        performanceTracker.addPerformance("Going through flight schedule response list");
        for (FlightScheduleResponse flightScheduleResponse : flightScheduleResponseList) {

            java.sql.Date startDateUtc = this.timeUtil.convertDDMMMYYToSQLDate(flightScheduleResponse.getPeriodOfOperationResponseUTC().getStartDate());
            java.sql.Date endDateUtc = this.timeUtil.convertDDMMMYYToSQLDate(flightScheduleResponse.getPeriodOfOperationResponseUTC().getEndDate());
            String operationDaysUtc = flightScheduleResponse.getPeriodOfOperationResponseUTC().getDaysOfOperation();
            java.sql.Date startDateLt = this.timeUtil.convertDDMMMYYToSQLDate(flightScheduleResponse.getPeriodOfOperationResponseLT().getStartDate());
            java.sql.Date endDateLt = this.timeUtil.convertDDMMMYYToSQLDate(flightScheduleResponse.getPeriodOfOperationResponseLT().getEndDate());
            String operationDaysLt = flightScheduleResponse.getPeriodOfOperationResponseLT().getDaysOfOperation();

            String operationPeriodKeyString = new FlightScheduleOperationPeriodKey(
                    startDateUtc.toString(), endDateUtc.toString(), new WeekRepresentation(operationDaysUtc), startDateLt.toString(), endDateLt.toString(), new WeekRepresentation(operationDaysLt)
            ).toString();

            FlightScheduleOperationPeriod operationPeriod = operationPeriodMap.computeIfAbsent(operationPeriodKeyString, key -> {
                FlightScheduleOperationPeriod newPeriod = new FlightScheduleOperationPeriod(
                        startDateUtc, endDateUtc, operationDaysUtc, startDateLt, endDateLt, operationDaysLt
                );
                return flightScheduleOperationPeriodRepository.save(newPeriod);
            });

            Airline airline = airlines.get(flightScheduleResponse.getAirline());

            FlightSchedule flightSchedule = flightScheduleRepository.save(
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
                Airport originAirport = airports.get(flightScheduleLeg.getOrigin());
                Airport destinationAirport = airports.get(flightScheduleLeg.getDestination());
                Airline aircraftOwnerAirline = airlines.get(flightScheduleLeg.getAircraftOwner());
                Aircraft aircraft = aircrafts.get(flightScheduleLeg.getAircraftType());

                CoordinatePair drawableCoordinates = MathUtil.calculateDrawableCoordinates(originAirport, destinationAirport);

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
                                TimeUtil.calculateDurationInMinutes(flightScheduleLeg),
                                MathUtil.calculateDistanceBetweenAirports(originAirport, destinationAirport),
                                drawableCoordinates.getOriginLongitude(),
                                drawableCoordinates.getDestinationLongitude()
                        )
                );
            });
        }
        // Batch inserts
        performanceTracker.addPerformance("Batch insert: flightScheduleDataElementRepository");
        flightScheduleDataElementRepository.saveAll(flightScheduleDataElements);

        performanceTracker.addPerformance("Batch insert: flightScheduleLegRepository");
        flightScheduleLegRepository.saveAll(flightScheduleLegs);

        this.flightScheduleCronRunRepository.updateCronRunFinish(cronRun.getCronRunId(), new Timestamp(new Date().getTime()));
    }

    private void ensureForeignKeyRelationInBatch(List<FlightScheduleResponse> flightScheduleResponseList, FlightScheduleCronRun cronRun, Set<String> airlineCodes, Set<String> airportCodes, Set<String> aircraftCodes) {
        aircraftCodes.remove(null);
        airportCodes.remove(null);
        airlineCodes.remove(null);

        cronRun.setAirlineCount(airlineCodes.size());
        cronRun.setAircraftCount(aircraftCodes.size());
        cronRun.setAirportCount(airportCodes.size());
        cronRun.setFlightScheduleCount(flightScheduleResponseList.size());
        this.flightScheduleCronRunRepository.save(cronRun);

        this.airlineService.ensureAirlinesExist(airlineCodes);
        this.aircraftService.ensureAircraftsExist(aircraftCodes);
        this.airportService.ensureAirportsExist(airportCodes);
    }

}
