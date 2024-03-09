package com.erijl.flightvisualizer.backend.repository;

import com.erijl.flightvisualizer.backend.dto.FlightDateFrequencyDto;
import com.erijl.flightvisualizer.backend.model.FlightScheduleOperationPeriod;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface FlightScheduleOperationPeriodRepository extends CrudRepository<FlightScheduleOperationPeriod, Integer> {

    @Query("SELECT new com.erijl.flightvisualizer.backend.dto.FlightDateFrequencyDto(f.startDateUtc, COUNT(f)) FROM FlightScheduleOperationPeriod f GROUP BY f.startDateUtc ORDER BY f.startDateUtc ASC")
    Iterable<FlightDateFrequencyDto> getFlightDateFrequency();
}