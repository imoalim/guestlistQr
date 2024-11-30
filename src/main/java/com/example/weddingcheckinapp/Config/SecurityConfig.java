package com.example.weddingcheckinapp.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF deaktivieren, falls nicht benötigt
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Alle Anfragen erlauben
                );

        return http.build();
    }
}

