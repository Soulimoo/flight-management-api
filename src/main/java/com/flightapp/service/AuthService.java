package com.flightapp.service;

import com.flightapp.dto.AuthRequest;
import com.flightapp.dto.AuthResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthResponse authenticate(AuthRequest authRequest);
}