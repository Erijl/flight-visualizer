package com.erijl.flightvisualizer.backend.model.repository;

import com.erijl.flightvisualizer.backend.model.entities.Airport;
import com.erijl.flightvisualizer.backend.model.projections.AirportRenderDataProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AirportRepository extends CrudRepository<Airport, String> {

    @Query(value = """
            select a.id as 'iataCode',
                   a.airport_name as 'airportName',
                   a.longitude as 'longitude',
                   a.latitude as 'latitude'
                   from airport a
                   where a.location_type = 'AIRPORT'
            """, nativeQuery = true)
    List<AirportRenderDataProjection> findAllAirportRenders();
}