package com.flightapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "FLIGHTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flight_seq")
    @SequenceGenerator(name = "flight_seq", sequenceName = "FLIGHT_SEQ", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 2)
    @Pattern(regexp = "^[A-Z0-9]{2}$", message = "Carrier code must be a valid 2-character IATA code")
    private String carrierCode;

    @Column(nullable = false, length = 4)
    @Pattern(regexp = "^\\d{4}$", message = "Flight number must be a 4-digit number")
    private String flightNumber;

    @Column(nullable = false)
    private LocalDate flightDate;

    @Column(nullable = false, length = 3)
    @Pattern(regexp = "^[A-Z]{3}$", message = "Origin must be a valid 3-character IATA airport code")
    private String origin;

    @Column(nullable = false, length = 3)
    @Pattern(regexp = "^[A-Z]{3}$", message = "Destination must be a valid 3-character IATA airport code")
    private String destination;
}
