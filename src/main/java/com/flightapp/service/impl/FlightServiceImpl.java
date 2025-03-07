package com.flightapp.service.impl;

import com.flightapp.dto.FlightDto;
import com.flightapp.exception.ResourceNotFoundException;
import com.flightapp.model.Flight;
import com.flightapp.repository.FlightRepository;
import com.flightapp.service.FlightService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    public FlightServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public FlightDto addFlight(FlightDto flightDto) {
        Flight flight = convertToEntity(flightDto);
        Flight savedFlight = flightRepository.save(flight);
        return convertToDto(savedFlight);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public FlightDto getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", id));
        return convertToDto(flight);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<FlightDto> getAllFlights() {
        List<Flight> flights = flightRepository.findAll();
        return flights.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteFlight(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", id));
        flightRepository.delete(flight);
    }

    private Flight convertToEntity(FlightDto flightDto) {
        Flight flight = new Flight();
        flight.setId(flightDto.getId());
        flight.setCarrierCode(flightDto.getCarrierCode());
        flight.setFlightNumber(flightDto.getFlightNumber());
        flight.setFlightDate(flightDto.getFlightDate());
        flight.setOrigin(flightDto.getOrigin());
        flight.setDestination(flightDto.getDestination());
        return flight;
    }

    private FlightDto convertToDto(Flight flight) {
        FlightDto flightDto = new FlightDto();
        flightDto.setId(flight.getId());
        flightDto.setCarrierCode(flight.getCarrierCode());
        flightDto.setFlightNumber(flight.getFlightNumber());
        flightDto.setFlightDate(flight.getFlightDate());
        flightDto.setOrigin(flight.getOrigin());
        flightDto.setDestination(flight.getDestination());
        return flightDto;
    }
}