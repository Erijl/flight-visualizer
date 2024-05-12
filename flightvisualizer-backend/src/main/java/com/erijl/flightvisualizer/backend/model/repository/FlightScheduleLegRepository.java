package com.erijl.flightvisualizer.backend.model.repository;

import com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.model.entities.FlightScheduleLeg;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;


public interface FlightScheduleLegRepository extends CrudRepository<FlightScheduleLeg, Integer> {

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
                select new com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegDto(fsl.originAirport, fsl.destinationAirport) from FlightScheduleLeg fsl
                              join FlightSchedule fs on fsl.flightSchedule = fs
                              join FlightScheduleOperationPeriod flop on fs.operationPeriod = flop
                              where fsl.originAirport is not null and fsl.destinationAirport is not null
            group by fsl.originAirport, fsl.destinationAirport
                """)
    List<FlightScheduleLegDto> findDistinctFlightScheduleLegsByStartAndEndDate();
}