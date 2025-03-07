package com.flightapp.config;

import com.flightapp.security.JwtAuthenticationFilter;
import com.flightapp.security.JwtTokenProvider;
import com.flightapp.security.UserDetailsServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public JwtTokenProvider jwtTokenProvider() {
        JwtTokenProvider mockProvider = mock(JwtTokenProvider.class);
        when(mockProvider.createToken(any(Authentication.class))).thenReturn("test-jwt-token");
        when(mockProvider.validateToken(any(String.class))).thenReturn(true);
        when(mockProvider.getUsernameFromToken(any(String.class))).thenReturn("testuser");
        return mockProvider;
    }

    @Bean
    @Primary
    public UserDetailsServiceImpl userDetailsService() {
        return mock(UserDetailsServiceImpl.class);
    }

    @Bean
    @Primary
    public JwtConfig jwtConfig() {
        JwtConfig config = mock(JwtConfig.class);
        when(config.getHeader()).thenReturn("Authorization");
        when(config.getPrefix()).thenReturn("Bearer ");
        when(config.getSecret()).thenReturn("testSecretKey12345678901234567890123456789012345678901234567890");
        when(config.getExpiration()).thenReturn(3600000L);
        return config;
    }

    @Bean
    @Primary
    public AuthenticationManager authenticationManager() {
        return mock(AuthenticationManager.class);
    }

    @Bean
    @Primary
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        JwtTokenProvider tokenProvider = jwtTokenProvider();
        UserDetailsServiceImpl userDetailsService = userDetailsService();
        JwtConfig jwtConfig = jwtConfig();
        return new JwtAuthenticationFilter(tokenProvider, userDetailsService, jwtConfig);
    }
}