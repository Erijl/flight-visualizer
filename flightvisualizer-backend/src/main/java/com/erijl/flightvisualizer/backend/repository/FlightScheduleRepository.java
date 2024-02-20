package com.erijl.flightvisualizer.backend.repository;

import com.erijl.flightvisualizer.backend.model.FlightSchedule;
import org.springframework.data.repository.CrudRepository;

public interface FlightScheduleRepository extends CrudRepository<FlightSchedule, Integer> {

}