package com.flightapp.controller;

import com.flightapp.dto.FlightDto;
import com.flightapp.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flights")
@Tag(name = "Flights", description = "Flight Management API for creating, retrieving, and deleting flight information")
@SecurityRequirement(name = "bearerAuth")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Add new flight",
            description = "Creates a new flight in the system. This operation requires ADMIN privileges. " +
                    "The flight details must include carrier code, flight number, date, origin, and destination."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Flight created successfully - Returns the created flight with generated ID",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FlightDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Created flight",
                                                    value = "{\n  \"id\": 1,\n  \"carrierCode\": \"AA\",\n  \"flightNumber\": \"1234\",\n  \"flightDate\": \"2025-03-15\",\n  \"origin\": \"JFK\",\n  \"destination\": \"LAX\"\n}",
                                                    description = "Example of a created flight"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid flight data - Request contains invalid or missing fields",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Requires ADMIN role",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<FlightDto> addFlight(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Flight details to be created",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = FlightDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "New flight",
                                            value = "{\n  \"carrierCode\": \"AA\",\n  \"flightNumber\": \"1234\",\n  \"flightDate\": \"2025-03-15\",\n  \"origin\": \"JFK\",\n  \"destination\": \"LAX\"\n}",
                                            description = "Example of a new flight request"
                                    )
                            }
                    )
            )
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
    @Operation(
            summary = "Get flight by ID",
            description = "Retrieves a specific flight by its unique identifier. " +
                    "Both ADMIN and USER roles can access this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Flight found - Returns the flight details",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FlightDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Flight details",
                                                    value = "{\n  \"id\": 1,\n  \"carrierCode\": \"AA\",\n  \"flightNumber\": \"1234\",\n  \"flightDate\": \"2025-03-15\",\n  \"origin\": \"JFK\",\n  \"destination\": \"LAX\"\n}",
                                                    description = "Example of flight details"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Flight not found - No flight exists with the provided ID",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<FlightDto> getFlightById(
            @Parameter(
                    description = "ID of the flight to retrieve",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {
        FlightDto flight = flightService.getFlightById(id);
        return ResponseEntity.ok(flight);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
            summary = "Get all flights",
            description = "Retrieves a list of all flights in the system. " +
                    "Both ADMIN and USER roles can access this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of flights retrieved successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = FlightDto.class)),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Flight list",
                                                    value = "[\n  {\n    \"id\": 1,\n    \"carrierCode\": \"AA\",\n    \"flightNumber\": \"1234\",\n    \"flightDate\": \"2025-03-15\",\n    \"origin\": \"JFK\",\n    \"destination\": \"LAX\"\n  },\n  {\n    \"id\": 2,\n    \"carrierCode\": \"BA\",\n    \"flightNumber\": \"4321\",\n    \"flightDate\": \"2025-03-16\",\n    \"origin\": \"LHR\",\n    \"destination\": \"JFK\"\n  }\n]",
                                                    description = "Example list of flights"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<List<FlightDto>> getAllFlights() {
        List<FlightDto> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete flight",
            description = "Permanently removes a flight from the system by its ID. " +
                    "This operation requires ADMIN privileges and cannot be undone."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Flight deleted successfully - No content returned",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Requires ADMIN role",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Flight not found - No flight exists with the provided ID",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<Void> deleteFlight(
            @Parameter(
                    description = "ID of the flight to delete",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {
        try {
            flightService.deleteFlight(id);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            throw e; // Re-throw for consistent handling by global exception handler
        }
    }

    // Special endpoint for admin access testing - Hidden from Swagger
    @Hidden
    @GetMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminOnlyEndpoint(@PathVariable Long id) {
        return ResponseEntity.ok("Admin access successful");
    }
}