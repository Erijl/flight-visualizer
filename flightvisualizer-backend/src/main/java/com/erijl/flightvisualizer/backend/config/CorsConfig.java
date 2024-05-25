package com.erijl.flightvisualizer.backend.config;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${flight.visualizer.cors.allowed.origins}")
    private String corsAllowedOrigins;

    @Value("${flight.visualizer.cors.allowed.methods}")
    private String corsAllowedMethods;

    @Value("${flight.visualizer.cors.allowed.headers}")
    private String corsAllowedHeaders;

    @Value("${flight.visualizer.cors.maxAge}")
    private int corsMaxAge;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(corsAllowedOrigins.split(","))
                        .allowedMethods(corsAllowedMethods.split(","))
                        .allowedHeaders(corsAllowedHeaders.split(","))
                        .maxAge(corsMaxAge);
            }
        };
    }
}
