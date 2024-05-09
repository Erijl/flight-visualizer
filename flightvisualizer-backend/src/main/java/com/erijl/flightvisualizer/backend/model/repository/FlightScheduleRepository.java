package com.erijl.flightvisualizer.backend.model.repository;

import com.erijl.flightvisualizer.backend.model.entities.FlightSchedule;
import org.springframework.data.repository.CrudRepository;

public interface FlightScheduleRepository extends CrudRepository<FlightSchedule, Integer> {

}