package com.erijl.flightvisualizer.backend.repository;

import com.erijl.flightvisualizer.backend.model.Airport;
import org.springframework.data.repository.CrudRepository;

public interface AirportRepository extends CrudRepository<Airport, Integer> {

}