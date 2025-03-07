package com.flightapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security configuration for tests that enables method-level security
 * This ensures @PreAuthorize annotations are evaluated properly in tests
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@Profile("test")
public class TestMethodSecurityConfig {

    /**
     * This bean ensures that method security works correctly in tests
     */
    @Bean
    public AuthenticationManager testAuthenticationManager() {
        return authentication -> {
            // In test environment, use the authentication that's already in the context
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            if (currentAuth != null) {
                return currentAuth;
            }
            return authentication;
        };
    }

    /**
     * Configures the method security expression handler to ensure
     * @PreAuthorize annotations are evaluated correctly in tests
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setDefaultRolePrefix("ROLE_");
        return expressionHandler;
    }
}