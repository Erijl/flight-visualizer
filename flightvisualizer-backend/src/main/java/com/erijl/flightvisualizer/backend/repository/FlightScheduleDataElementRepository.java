package com.erijl.flightvisualizer.backend.repository;

import com.erijl.flightvisualizer.backend.model.FlightScheduleDataElement;
import org.springframework.data.repository.CrudRepository;

public interface FlightScheduleDataElementRepository extends CrudRepository<FlightScheduleDataElement, Integer> {

}