package com.erijl.flightvisualizer.backend.repository;

import com.erijl.flightvisualizer.backend.model.FlightScheduleCronRun;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

public interface FlightScheduleCronRunRepository extends CrudRepository<FlightScheduleCronRun, Integer> {

    FlightScheduleCronRun findFlightScheduleCronRunByCronRunDateUtcEquals(String cronRunDateUtc);

    @Transactional
    @Modifying
    @Query("update FlightScheduleCronRun f set f.cronRunFinish = ?2 where f.cronRunId = ?1")
    void updateCronRunFinish(Integer cronRunId, Timestamp cronRunFinish);
}
