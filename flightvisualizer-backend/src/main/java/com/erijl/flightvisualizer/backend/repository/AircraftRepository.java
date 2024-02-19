package com.erijl.flightvisualizer.backend.repository;

import com.erijl.flightvisualizer.backend.model.Aircraft;
import org.springframework.data.repository.CrudRepository;

public interface AircraftRepository extends CrudRepository<Aircraft, Integer> {

}