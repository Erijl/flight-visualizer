package com.erijl.flightvisualizer.backend.repository;

import com.erijl.flightvisualizer.backend.model.FlightScheduleLeg;
import org.springframework.data.repository.CrudRepository;

public interface FlightScheduleLegRepository extends CrudRepository<FlightScheduleLeg, Integer> {

}