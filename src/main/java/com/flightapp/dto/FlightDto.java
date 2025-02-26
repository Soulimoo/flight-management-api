package com.flightapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDto {

    private Long id;

    @NotBlank(message = "Carrier code is required")
    @Pattern(regexp = "^[A-Z0-9]{2}$", message = "Carrier code must be a valid 2-character IATA code")
    private String carrierCode;

    @NotBlank(message = "Flight number is required")
    @Pattern(regexp = "^\\d{4}$", message = "Flight number must be a 4-digit number")
    private String flightNumber;

    @NotNull(message = "Flight date is required")
    private LocalDate flightDate;

    @NotBlank(message = "Origin is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Origin must be a valid 3-character IATA airport code")
    private String origin;

    @NotBlank(message = "Destination is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Destination must be a valid 3-character IATA airport code")
    private String destination;
}