package com.flightapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flightapp.config.TestConfig;
import com.flightapp.config.TestMethodSecurityConfig;
import com.flightapp.config.TestSecurityConfig;
import com.flightapp.dto.FlightDto;
import com.flightapp.exception.ResourceNotFoundException;
import com.flightapp.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
@Import({TestConfig.class, TestSecurityConfig.class, TestMethodSecurityConfig.class})
public class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    private ObjectMapper objectMapper;
    private FlightDto flightDto1;
    private FlightDto flightDto2;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Setup test data
        LocalDate testDate = LocalDate.of(2025, 3, 15);

        flightDto1 = new FlightDto();
        flightDto1.setId(1L);
        flightDto1.setCarrierCode("AA");
        flightDto1.setFlightNumber("1234");
        flightDto1.setFlightDate(testDate);
        flightDto1.setOrigin("JFK");
        flightDto1.setDestination("LHR");

        flightDto2 = new FlightDto();
        flightDto2.setId(2L);
        flightDto2.setCarrierCode("BA");
        flightDto2.setFlightNumber("4321");
        flightDto2.setFlightDate(testDate);
        flightDto2.setOrigin("LHR");
        flightDto2.setDestination("JFK");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addFlight_ValidData_ShouldReturnCreated() throws Exception {
        // Arrange
        when(flightService.addFlight(any(FlightDto.class))).thenReturn(flightDto1);

        // Act & Assert
        mockMvc.perform(post("/flights")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flightDto1)))
                .andExpect(status().isCreated());

        verify(flightService, times(1)).addFlight(any(FlightDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void addFlight_AsUser_ShouldReturnForbidden() throws Exception {
        // Since the @PreAuthorize annotation is blocking before the service call,
        // we'll verify the authorization works but we can't verify the service method

        // Instead of verifying the service was called, we'll verify we get a 403 forbidden
        mockMvc.perform(post("/flights")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flightDto1)))
                .andExpect(status().isForbidden());

        // No need to verify service interaction since security prevents it from being called
        // That's actually the correct behavior
    }

    @Test
    @WithMockUser(roles = "USER")
    void getFlightById_ExistingId_ShouldReturnFlight() throws Exception {
        // Arrange
        when(flightService.getFlightById(1L)).thenReturn(flightDto1);

        // Act & Assert
        mockMvc.perform(get("/flights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(flightDto1.getId()))
                .andExpect(jsonPath("$.carrierCode").value(flightDto1.getCarrierCode()))
                .andExpect(jsonPath("$.flightNumber").value(flightDto1.getFlightNumber()));

        verify(flightService, times(1)).getFlightById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getFlightById_NonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(flightService.getFlightById(999L))
                .thenThrow(new ResourceNotFoundException("Flight", "id", 999L));

        // Act & Assert
        mockMvc.perform(get("/flights/999"))
                .andExpect(status().isNotFound());

        verify(flightService, times(1)).getFlightById(999L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllFlights_ShouldReturnAllFlights() throws Exception {
        // Arrange
        List<FlightDto> flightList = new ArrayList<>();
        flightList.add(flightDto1);
        flightList.add(flightDto2);

        when(flightService.getAllFlights()).thenReturn(flightList);

        // Act & Assert
        mockMvc.perform(get("/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(flightDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(flightDto2.getId()));

        verify(flightService, times(1)).getAllFlights();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteFlight_ExistingId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(flightService).deleteFlight(1L);

        // Act & Assert
        mockMvc.perform(delete("/flights/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());

        verify(flightService, times(1)).deleteFlight(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteFlight_AsUser_ShouldReturnForbidden() throws Exception {
        // Similar to addFlight_AsUser_ShouldReturnForbidden test
        // Since security blocks before service is called, just verify access is forbidden

        mockMvc.perform(delete("/flights/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden());

        // No need to verify service calls since security prevents them
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteFlight_NonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Flight", "id", 999L))
                .when(flightService).deleteFlight(999L);

        // Act & Assert
        mockMvc.perform(delete("/flights/999")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());

        verify(flightService, times(1)).deleteFlight(999L);
    }
}