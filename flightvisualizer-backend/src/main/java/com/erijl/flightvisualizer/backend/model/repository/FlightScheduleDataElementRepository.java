package com.erijl.flightvisualizer.backend.model.repository;

import com.erijl.flightvisualizer.backend.model.entities.FlightScheduleDataElement;
import org.springframework.data.repository.CrudRepository;

public interface FlightScheduleDataElementRepository extends CrudRepository<FlightScheduleDataElement, Integer> {

}