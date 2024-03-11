package com.erijl.flightvisualizer.backend.repository;

import com.erijl.flightvisualizer.backend.dto.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.model.FlightScheduleLeg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;

public interface FlightScheduleLegRepository extends CrudRepository<FlightScheduleLeg, Integer> {
    Page<FlightScheduleLeg> findAll(Pageable pageable);

    @Query("SELECT new com.erijl.flightvisualizer.backend.dto.FlightScheduleLegDto(fsl.legId, fsl.originAirport, fsl.destinationAirport) " +
            "FROM FlightScheduleLeg fsl " +
            "JOIN FlightSchedule fs ON fsl.flightSchedule = fs " +
            "JOIN FlightScheduleOperationPeriod flop ON fs.operationPeriod = flop " +
            "WHERE flop.startDateUtc >= :startDate AND flop.endDateUtc <= :endDate")
    Iterable<FlightScheduleLegDto> findAllWithoutAssociationsByStartAndEndDate(Date startDate, Date endDate);

    @Query("SELECT new com.erijl.flightvisualizer.backend.dto.FlightScheduleLegDto(fsl.legId, fsl.originAirport, fsl.destinationAirport) " +
            "FROM FlightScheduleLeg fsl " +
            "JOIN FlightSchedule fs ON fsl.flightSchedule = fs " +
            "JOIN FlightScheduleOperationPeriod flop ON fs.operationPeriod = flop " +
            "WHERE flop.startDateUtc = :date AND flop.endDateUtc = :date")
    Iterable<FlightScheduleLegDto> findAllWithoutAssociationsBySingleDate(Date date);
}