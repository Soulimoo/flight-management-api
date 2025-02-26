package com.flightapp.service;

import com.flightapp.dto.FlightDto;

import java.util.List;

public interface FlightService {
    FlightDto addFlight(FlightDto flightDto);
    FlightDto getFlightById(Long id);
    List<FlightDto> getAllFlights();
    void deleteFlight(Long id);
}