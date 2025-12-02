package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para pruebas
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll() // Permite todo
                .anyRequest().permitAll()
            )
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                corsConfig.addAllowedOrigin("*");
                corsConfig.addAllowedHeader("*");
                corsConfig.addAllowedMethod("*");
                return corsConfig;
            }));
        
        return http.build();
    }
}