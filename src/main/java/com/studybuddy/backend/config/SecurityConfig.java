/**
 * SECURITY CONFIG TO ALLOW DEVELOPMENT -- REMOVE AT PRODUCTION.
 */

package com.studybuddy.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/hello").permitAll() // Allow unauthenticated access
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable()); // Disable CSRF for simplicity in testing

        return http.build();
    }
}
