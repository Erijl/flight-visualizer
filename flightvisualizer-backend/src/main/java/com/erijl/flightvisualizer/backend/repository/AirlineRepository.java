package com.erijl.flightvisualizer.backend.repository;

import com.erijl.flightvisualizer.backend.model.Airline;
import org.springframework.data.repository.CrudRepository;

public interface AirlineRepository extends CrudRepository<Airline, Integer> {

}