package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.model.entities.FlightSchedule;
import com.erijl.flightvisualizer.backend.model.repository.FlightScheduleRepository;
import org.springframework.stereotype.Service;

@Service
public class FlightScheduleService {

    private final FlightScheduleRepository flightScheduleRepository;

    public FlightScheduleService(FlightScheduleRepository flightScheduleRepository) {
        this.flightScheduleRepository = flightScheduleRepository;
    }

    public FlightSchedule getFlightScheduleById(int id) {
        return flightScheduleRepository.findById(id).orElse(null);
    }
}
