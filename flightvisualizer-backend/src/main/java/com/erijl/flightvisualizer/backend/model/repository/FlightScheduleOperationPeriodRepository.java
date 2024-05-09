package com.erijl.flightvisualizer.backend.model.repository;

import com.erijl.flightvisualizer.backend.model.dtos.FlightDateFrequencyDto;
import com.erijl.flightvisualizer.backend.model.entities.FlightScheduleOperationPeriod;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface FlightScheduleOperationPeriodRepository extends CrudRepository<FlightScheduleOperationPeriod, Integer> {

    @Query("SELECT new com.erijl.flightvisualizer.backend.model.dtos.FlightDateFrequencyDto(f.startDateUtc, COUNT(f)) FROM FlightScheduleOperationPeriod f GROUP BY f.startDateUtc ORDER BY f.startDateUtc ASC")
    Iterable<FlightDateFrequencyDto> getFlightDateFrequency();
}