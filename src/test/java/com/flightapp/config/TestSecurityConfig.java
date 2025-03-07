package com.flightapp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@TestConfiguration
@EnableMethodSecurity
public class TestSecurityConfig {

    /**
     * Creates a security filter chain for testing with CSRF disabled
     * @param http HttpSecurity to configure
     * @return Configured SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChainTest(HttpSecurity http) throws Exception {
        // Disable CSRF for testing but keep security mechanisms active
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/h2-console/**").permitAll()
                        .requestMatchers("/flights/**").authenticated()
                        .anyRequest().authenticated()
                )
                // Use consistent responses for authenticated users with insufficient permissions
                .exceptionHandling(exceptions ->
                        exceptions.accessDeniedHandler((request, response, ex) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"status\":403,\"message\":\"Access denied\"}");
                        })
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}