package com.erijl.flightvisualizer.backend.config;

import com.erijl.flightvisualizer.backend.manager.AuthManager;
import com.erijl.flightvisualizer.backend.model.api.FlightScheduleResponse;
import com.erijl.flightvisualizer.backend.model.api.LegResponse;
import com.erijl.flightvisualizer.backend.model.entities.*;
import com.erijl.flightvisualizer.backend.model.internal.CoordinatePair;
import com.erijl.flightvisualizer.backend.model.repository.*;
import com.erijl.flightvisualizer.backend.service.AircraftService;
import com.erijl.flightvisualizer.backend.service.AirlineService;
import com.erijl.flightvisualizer.backend.service.AirportService;
import com.erijl.flightvisualizer.backend.service.FlightScheduleOperationPeriodService;
import com.erijl.flightvisualizer.backend.util.TimeUtil;
import com.erijl.flightvisualizer.backend.util.MathUtil;
import com.erijl.flightvisualizer.backend.util.RestUtil;
import com.erijl.flightvisualizer.backend.util.UrlBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.Duration;
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
    private final FlightScheduleOperationPeriodService flightScheduleOperationPeriodService;

    private final FlightScheduleCronRunRepository flightScheduleCronRunRepository;

    private final FlightScheduleRepository flightScheduleRepository;
    private final FlightScheduleOperationPeriodRepository flightScheduleOperationPeriodRepository;
    private final FlightScheduleDataElementRepository flightScheduleDataElementRepository;
    private final FlightScheduleLegRepository flightScheduleLegRepository;
    private final Gson gson = new GsonBuilder().create();


    //TODO remove java.util.date globally
    //TODO use MapStruct for conversion here
    //TODO make TimeUtil static
    public CronScheduler(TimeUtil timeUtil, RestUtil restUtil, AirlineService airlineService, AircraftService aircraftService, AirportService airportService, FlightScheduleOperationPeriodService flightScheduleOperationPeriodService, FlightScheduleRepository flightScheduleRepository, AuthManager authManager, FlightScheduleCronRunRepository flightScheduleCronRunRepository, FlightScheduleOperationPeriodRepository flightScheduleOperationPeriodRepository, FlightScheduleDataElementRepository flightScheduleDataElementRepository, FlightScheduleLegRepository flightScheduleLegRepository) {
        this.timeUtil = timeUtil;
        this.restUtil = restUtil;
        this.airlineService = airlineService;
        this.aircraftService = aircraftService;
        this.airportService = airportService;
        this.flightScheduleOperationPeriodService = flightScheduleOperationPeriodService;
        this.flightScheduleRepository = flightScheduleRepository;
        this.authManager = authManager;
        this.flightScheduleCronRunRepository = flightScheduleCronRunRepository;
        this.flightScheduleOperationPeriodRepository = flightScheduleOperationPeriodRepository;
        this.flightScheduleDataElementRepository = flightScheduleDataElementRepository;
        this.flightScheduleLegRepository = flightScheduleLegRepository;
    }


    //@Scheduled(initialDelay = 1000)
    //public void fetchOldFlightSchedules() {
    //    LocalDate startDate = LocalDate.of(2024, 4, 6);
    //    LocalDate endDate = LocalDate.of(2024, 5, 10);
    //    LocalDate currentDate = startDate;
//
    //    while (!currentDate.isAfter(endDate)) {
    //        try {
    //            log.info("[{}]: Fetching flight schedule for {}", LocalDate.now(), currentDate);
    //            fetchTodaysFlightSchedule(currentDate);
    //        } catch (Exception e) {
    //            log.error("Error fetching flight schedule for {}", currentDate, e); // Log the error
    //        }
//
    //        try {
    //            Thread.sleep(60 * 1000);
    //        } catch (InterruptedException e) {
    //            log.warn("Delay interrupted", e);
    //        }
//
    //        currentDate = currentDate.plusDays(1);
    //    }
    //}

    @Scheduled(initialDelay = 1000)
    public void fetchCurrentFlightSchedule() {
        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC"));

        try {
            log.info("[{}]: Fetching flight schedule for {}", LocalDate.now(), currentDate);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            fetchFlightSchedule(currentDate);

            stopWatch.stop();
            long seconds = Duration.ofMillis(stopWatch.getTotalTimeMillis()).getSeconds();
            long HH = seconds / 3600;
            long MM = (seconds % 3600) / 60;
            long SS = seconds % 60;
            String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);
            log.info("Flight schedule for {} fetched in {}", currentDate, timeInHHMMSS);
        } catch (Exception e) {
            log.error("Error fetching flight schedule for {}", currentDate, e);
        }
    }

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
        /**
         * Current execution times:
         * operation period insertion	11:12 min (can be optimized below 1 min)
         * flight schedule insertion	01:29 min // fine
         * data_element insertion 		11:39 min (doubt optimization possible)
         * flight leg insertion		    01:50 min // fine
         * --------------------------------------
         * total time			        26:10 min
         */
        List<FlightSchedule> flightSchedules = new ArrayList<>();
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

        this.ensureForeignKeyRelationInBatch(flightScheduleResponseList, cronRun, airlineCodes, airportCodes, aircraftCodes);

        Map<String, Airline> airlines = airlineService.getAirlinesById(airlineCodes);
        Map<String, Airport> airports = airportService.getAirportsById(airportCodes);
        Map<String, Aircraft> aircrafts = aircraftService.getAircraftsById(aircraftCodes);

        for (FlightScheduleResponse flightScheduleResponse : flightScheduleResponseList) {
            FlightScheduleOperationPeriod operationPeriod = flightScheduleOperationPeriodRepository.save(
                    new FlightScheduleOperationPeriod(
                            this.timeUtil.convertDDMMMYYToSQLDate(flightScheduleResponse.getPeriodOfOperationResponseUTC().getStartDate()),
                            this.timeUtil.convertDDMMMYYToSQLDate(flightScheduleResponse.getPeriodOfOperationResponseUTC().getEndDate()),
                            flightScheduleResponse.getPeriodOfOperationResponseUTC().getDaysOfOperation(),
                            this.timeUtil.convertDDMMMYYToSQLDate(flightScheduleResponse.getPeriodOfOperationResponseLT().getStartDate()),
                            this.timeUtil.convertDDMMMYYToSQLDate(flightScheduleResponse.getPeriodOfOperationResponseLT().getEndDate()),
                            flightScheduleResponse.getPeriodOfOperationResponseLT().getDaysOfOperation()
                    )
            );

            Airline airline = airlines.get(flightScheduleResponse.getAirline());

            FlightSchedule flightSchedule = flightScheduleRepository.save(
                    new FlightSchedule(
                            airline,
                            operationPeriod,
                            flightScheduleResponse.getFlightNumber(),
                            flightScheduleResponse.getSuffix()
                    )
            );
            flightSchedules.add(flightSchedule);

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
        flightScheduleRepository.saveAll(flightSchedules);
        flightScheduleDataElementRepository.saveAll(flightScheduleDataElements);
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
