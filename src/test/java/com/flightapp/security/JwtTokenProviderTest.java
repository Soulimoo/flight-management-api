package com.flightapp.security;

import com.flightapp.config.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtTokenProviderTest {

    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private JwtTokenProvider tokenProvider;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Setup mock JwtConfig
        String longSecret = "testSecretKey12345678901234567890123456789012345678901234567890";
        lenient().when(jwtConfig.getSecret()).thenReturn(Base64.getEncoder().encodeToString(longSecret.getBytes()));
        lenient().when(jwtConfig.getExpiration()).thenReturn(3600000L);

        // Create a real UserDetails object for testing
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();

        // Create a real Authentication object using the UserDetails
        authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Initialize token provider manually
        try {
            tokenProvider.init();
        } catch (Exception e) {
            // In case init() has already been called or there's an issue
            System.out.println("Note: " + e.getMessage());
        }
    }

    @Test
    void createToken_ShouldReturnValidToken() {
        // Act
        String token = tokenProvider.createToken(authentication);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getUsernameFromToken_ShouldReturnCorrectUsername() {
        // Arrange
        String token = tokenProvider.createToken(authentication);

        // Act
        String username = tokenProvider.getUsernameFromToken(token);

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void validateToken_ValidToken_ShouldReturnTrue() {
        // Arrange
        String token = tokenProvider.createToken(authentication);

        // Act
        boolean isValid = tokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken_ShouldReturnFalse() {
        // Arrange
        String invalidToken = "invalid.token.string";

        // Act
        boolean isValid = tokenProvider.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }
}