package com.erijl.flightvisualizer.backend.repository;

import com.erijl.flightvisualizer.backend.dto.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.model.FlightScheduleLeg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface FlightScheduleLegRepository extends CrudRepository<FlightScheduleLeg, Integer> {
    Page<FlightScheduleLeg> findAll(Pageable pageable);

    @Query("SELECT new com.erijl.flightvisualizer.backend.dto.FlightScheduleLegDto(fsl.legId, fsl.originAirport, fsl.destinationAirport) FROM FlightScheduleLeg fsl")
    Iterable<FlightScheduleLegDto> findAllWithoutAssociations();
}