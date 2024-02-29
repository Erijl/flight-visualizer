package com.erijl.flightvisualizer.backend.repository;

import com.erijl.flightvisualizer.backend.model.FlightScheduleCronRun;
import org.springframework.data.repository.CrudRepository;

public interface FlightScheduleCronRunRepository extends CrudRepository<FlightScheduleCronRun, Integer> {

    FlightScheduleCronRun findFlightScheduleCronRunByCronRunDateUtcEquals(String cronRunDateUtc);
}
