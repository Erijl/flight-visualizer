package com.erijl.flightvisualizer.backend.model.repository;

import com.erijl.flightvisualizer.backend.model.entities.FlightScheduleOperationPeriod;
import com.erijl.flightvisualizer.backend.model.projections.FlightDateFrequencyProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FlightScheduleOperationPeriodRepository extends CrudRepository<FlightScheduleOperationPeriod, Integer> {

    @Query(value = """
            select
                DATE_FORMAT(fsop.start_date_utc, '%Y-%m-%d') as 'startDateUtc',
                count(fsop.start_date_utc) as 'flightCount'
            from flight_schedule_operation_period fsop
            group by fsop.start_date_utc
            order by fsop.start_date_utc desc
                """, nativeQuery = true)
    List<FlightDateFrequencyProjection> getFlightDateFrequency();
}