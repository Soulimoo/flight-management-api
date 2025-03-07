package com.flightapp.controller;

import com.flightapp.dto.FlightDto;
import com.flightapp.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flights")
@Tag(name = "Flights", description = "Flight Management API")
@SecurityRequirement(name = "bearerAuth")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add new flight", description = "Create a new flight (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Flight created successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FlightDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid flight data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role")
    })
    public ResponseEntity<FlightDto> addFlight(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Flight details",
                    content = @Content(schema = @Schema(implementation = FlightDto.class)))
            FlightDto flightDto) {
        try {
            FlightDto savedFlight = flightService.addFlight(flightDto);
            return new ResponseEntity<>(savedFlight, HttpStatus.CREATED);
        } catch (AccessDeniedException e) {
            throw e; // Re-throw for consistent handling by global exception handler
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get flight by ID", description = "Retrieve a specific flight by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FlightDto.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    public ResponseEntity<FlightDto> getFlightById(
            @Parameter(description = "ID of the flight to retrieve", required = true)
            @PathVariable Long id) {
        FlightDto flight = flightService.getFlightById(id);
        return ResponseEntity.ok(flight);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get all flights", description = "Retrieve a list of all flights")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of flights retrieved",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FlightDto.class))) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<FlightDto>> getAllFlights() {
        List<FlightDto> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete flight", description = "Delete a flight by its ID (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Flight deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    public ResponseEntity<Void> deleteFlight(
            @Parameter(description = "ID of the flight to delete", required = true)
            @PathVariable Long id) {
        try {
            flightService.deleteFlight(id);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            throw e; // Re-throw for consistent handling by global exception handler
        }
    }

    // Special endpoint for admin access testing
    @GetMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminOnlyEndpoint(@PathVariable Long id) {
        return ResponseEntity.ok("Admin access successful");
    }
}