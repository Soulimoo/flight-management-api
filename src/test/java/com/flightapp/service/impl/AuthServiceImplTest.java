package com.flightapp.service.impl;

import com.flightapp.dto.AuthRequest;
import com.flightapp.dto.AuthResponse;
import com.flightapp.repository.UserRepository;
import com.flightapp.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        authRequest = new AuthRequest("testuser", "password");
    }

    @Test
    void authenticate_ShouldReturnAuthResponse() {
        // Arrange
        // Create a mock Authentication that returns the role we want
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenAnswer(invocation -> {
                    // Return a custom implementation of Authentication interface
                    return new Authentication() {
                        @Override
                        public String getName() {
                            return "testuser";
                        }

                        // Only implement the method that our service uses
                        @Override
                        public Object getCredentials() {
                            return null;
                        }

                        @Override
                        public Object getDetails() {
                            return null;
                        }

                        @Override
                        public Object getPrincipal() {
                            return "testuser";
                        }

                        @Override
                        public boolean isAuthenticated() {
                            return true;
                        }

                        @Override
                        public void setAuthenticated(boolean isAuthenticated) {
                        }

                        @Override
                        public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
                            return java.util.Collections.singletonList(
                                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")
                            );
                        }
                    };
                });

        when(tokenProvider.createToken(any(Authentication.class))).thenReturn("test-jwt-token");

        // Act
        AuthResponse response = authService.authenticate(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("ADMIN", response.getRole());
    }
}