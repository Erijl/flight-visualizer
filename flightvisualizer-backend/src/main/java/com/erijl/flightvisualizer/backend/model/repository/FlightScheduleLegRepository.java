package com.erijl.flightvisualizer.backend.model.repository;

import com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.model.entities.FlightScheduleLeg;
import com.erijl.flightvisualizer.backend.model.projections.LegRenderDataProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;


public interface FlightScheduleLegRepository extends JpaRepository<FlightScheduleLeg, Integer> {

    @Query("SELECT new com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegDto(fsl.legId, fsl.flightSchedule.flightScheduleId, fsl.originAirport, fsl.destinationAirport, fsl.aircraftDepartureTimeUtc, fsl.aircraftDepartureTimeDateDiffUtc, fsl.aircraftArrivalTimeUtc, fsl.aircraftArrivalTimeDateDiffUtc) " +
            "FROM FlightScheduleLeg fsl " +
            "JOIN FlightSchedule fs ON fsl.flightSchedule = fs " +
            "JOIN FlightScheduleOperationPeriod flop ON fs.operationPeriod = flop " +
            "WHERE flop.startDateUtc >= :startDate AND flop.endDateUtc <= :endDate")
    Iterable<FlightScheduleLegDto> findAllWithoutAssociationsByStartAndEndDate(Date startDate, Date endDate);

    @Query("SELECT new com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegDto(fsl.legId, fsl.flightSchedule.flightScheduleId, fsl.originAirport, fsl.destinationAirport, fsl.aircraftDepartureTimeUtc, fsl.aircraftDepartureTimeDateDiffUtc, fsl.aircraftArrivalTimeUtc, fsl.aircraftArrivalTimeDateDiffUtc) " +
            "FROM FlightScheduleLeg fsl " +
            "JOIN FlightSchedule fs ON fsl.flightSchedule = fs " +
            "JOIN FlightScheduleOperationPeriod flop ON fs.operationPeriod = flop " +
            "WHERE flop.startDateUtc = :date AND flop.endDateUtc = :date")
    Iterable<FlightScheduleLegDto> findAllWithoutAssociationsBySingleDate(Date date);

    @Query(value = """
                SELECT fsl.origin_airport                               AS 'originAirportIataCode',
                       fsl.destination_airport                          AS 'destinationAirportIataCode',
                       fsl.drawable_origin_longitude                    AS 'originLongitude',
                       a_origin.latitude                                AS 'originLatitude',
                       fsl.drawable_destination_longitude               AS 'destinationLongitude',
                       a_destination.latitude                           AS 'destinationLatitude',
                       a_origin.iso_country_code                        AS 'originIsoCountryCode',
                       a_destination.iso_country_code                   AS 'destinationIsoCountryCode',
                       a_origin.timezone_id                             AS 'originTimezoneId',
                       a_destination.timezone_id                        AS 'destinationTimezoneId',
                       a_origin.offset_utc                              AS 'originOffsetUtc',
                       a_destination.offset_utc                         AS 'destinationOffsetUtc',
                       fsl.aircraft_departure_time_date_diff_utc        AS 'aircraftDepartureTimeDateDiffUtc',
                       fsl.aircraft_arrival_time_date_diff_utc          AS 'aircraftArrivalTimeDateDiffUtc',
                       fsl.aircraft_arrival_time_utc                    AS 'aircraftArrivalTimeUtc',
                       fsl.aircraft_departure_time_utc                  AS 'aircraftDepartureTimeUtc',
                       fsl.duration_minutes                             AS 'durationMinutes',
                       fsl.distance_kilometers                          AS 'distanceKilometers',
                       a_origin.airport_name                            AS 'originAirportName',
                       a_destination.airport_name                       AS 'destinationAirportName'
                FROM flight_schedule_leg fsl
                         JOIN flight_schedule fs ON fs.id = fsl.flight_schedule_id
                         JOIN flight_schedule_operation_period flop ON flop.id = fs.operation_period_id
                         JOIN airport a_origin ON a_origin.id = fsl.origin_airport
                         JOIN airport a_destination ON a_destination.id = fsl.destination_airport
                WHERE (
                    -- Case 1: Only startDate
                    (:startDate IS NOT NULL AND :endDate IS NULL AND flop.start_date_utc = :startDate)
                        OR
                        -- Case 2: Only endDate
                    (:startDate IS NULL AND :endDate IS NOT NULL AND flop.end_date_utc = :endDate)
                        OR
                        -- Case 3: Both startDate and endDate
                    (:startDate IS NOT NULL AND :endDate IS NOT NULL AND flop.start_date_utc >= :startDate AND
                     flop.end_date_utc <= :endDate)
                    )
                    AND a_origin.id != 'RMO' AND a_destination.id != 'RMO'
                    AND a_origin.location_type = 'Airport' AND a_destination.location_type = 'Airport'
                GROUP BY fsl.origin_airport, fsl.destination_airport
            """, nativeQuery = true)//TODO check RMO at some point
    List<LegRenderDataProjection> findDistinctFlightScheduleLegsByStartAndEndDate(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}