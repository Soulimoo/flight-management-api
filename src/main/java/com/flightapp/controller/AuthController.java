package com.flightapp.controller;

import com.flightapp.dto.AuthRequest;
import com.flightapp.dto.AuthResponse;
import com.flightapp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "API for user authentication and token management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user with username and password credentials and returns a JWT token " +
                    "that can be used for subsequent authorized requests. The token is valid for 1 hour."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful - Returns JWT token, username and role",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Admin login response",
                                                    value = "{\n  \"token\": \"eyJhbGciOiJIUzI1NiJ9...\",\n  \"username\": \"admin\",\n  \"role\": \"ADMIN\"\n}",
                                                    description = "Response for admin user login"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials - Username or password is incorrect",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - Missing username or password",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<AuthResponse> authenticateUser(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User credentials",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AuthRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Admin login",
                                            value = "{\n  \"username\": \"admin\",\n  \"password\": \"123\"\n}",
                                            description = "Login request for admin user"
                                    ),
                                    @ExampleObject(
                                            name = "Regular user login",
                                            value = "{\n  \"username\": \"user\",\n  \"password\": \"123\"\n}",
                                            description = "Login request for regular user"
                                    )
                            }
                    )
            )
            AuthRequest authRequest) {
        AuthResponse response = authService.authenticate(authRequest);
        return ResponseEntity.ok(response);
    }
}