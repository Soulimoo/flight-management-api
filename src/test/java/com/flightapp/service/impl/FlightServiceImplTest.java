package com.flightapp.service.impl;

import com.flightapp.dto.FlightDto;
import com.flightapp.exception.ResourceNotFoundException;
import com.flightapp.model.Flight;
import com.flightapp.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightServiceImpl flightService;

    private Flight flight1;
    private Flight flight2;
    private FlightDto flightDto;

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDate testDate = LocalDate.of(2025, 3, 15);

        flight1 = new Flight();
        flight1.setId(1L);
        flight1.setCarrierCode("AA");
        flight1.setFlightNumber("1234");
        flight1.setFlightDate(testDate);
        flight1.setOrigin("JFK");
        flight1.setDestination("LHR");

        flight2 = new Flight();
        flight2.setId(2L);
        flight2.setCarrierCode("BA");
        flight2.setFlightNumber("4321");
        flight2.setFlightDate(testDate);
        flight2.setOrigin("LHR");
        flight2.setDestination("JFK");

        flightDto = new FlightDto();
        flightDto.setCarrierCode("AA");
        flightDto.setFlightNumber("1234");
        flightDto.setFlightDate(testDate);
        flightDto.setOrigin("JFK");
        flightDto.setDestination("LHR");
    }

    @Test
    void addFlight_ShouldReturnSavedFlightDto() {
        // Arrange
        when(flightRepository.save(any(Flight.class))).thenReturn(flight1);

        // Act
        FlightDto savedFlight = flightService.addFlight(flightDto);

        // Assert
        assertNotNull(savedFlight);
        assertEquals(flight1.getId(), savedFlight.getId());
        assertEquals(flight1.getCarrierCode(), savedFlight.getCarrierCode());
        assertEquals(flight1.getFlightNumber(), savedFlight.getFlightNumber());
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void getFlightById_ExistingId_ShouldReturnFlightDto() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight1));

        // Act
        FlightDto foundFlight = flightService.getFlightById(1L);

        // Assert
        assertNotNull(foundFlight);
        assertEquals(flight1.getId(), foundFlight.getId());
        assertEquals(flight1.getCarrierCode(), foundFlight.getCarrierCode());
        assertEquals(flight1.getOrigin(), foundFlight.getOrigin());
        verify(flightRepository, times(1)).findById(1L);
    }

    @Test
    void getFlightById_NonExistingId_ShouldThrowException() {
        // Arrange
        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> flightService.getFlightById(999L));
        verify(flightRepository, times(1)).findById(999L);
    }

    @Test
    void getAllFlights_ShouldReturnAllFlights() {
        // Arrange
        List<Flight> flightList = new ArrayList<>();
        flightList.add(flight1);
        flightList.add(flight2);
        when(flightRepository.findAll()).thenAnswer(invocation -> flightList);

        // Act
        List<FlightDto> flights = flightService.getAllFlights();

        // Assert
        assertNotNull(flights);
        assertEquals(2, flights.size());
        assertEquals(flight1.getId(), flights.get(0).getId());
        assertEquals(flight2.getId(), flights.get(1).getId());
        verify(flightRepository, times(1)).findAll();
    }

    @Test
    void deleteFlight_ExistingId_ShouldDeleteFlight() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight1));
        doNothing().when(flightRepository).delete(flight1);

        // Act
        flightService.deleteFlight(1L);

        // Assert
        verify(flightRepository, times(1)).findById(1L);
        verify(flightRepository, times(1)).delete(flight1);
    }

    @Test
    void deleteFlight_NonExistingId_ShouldThrowException() {
        // Arrange
        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> flightService.deleteFlight(999L));
        verify(flightRepository, times(1)).findById(999L);
        verify(flightRepository, never()).delete(any(Flight.class));
    }
}