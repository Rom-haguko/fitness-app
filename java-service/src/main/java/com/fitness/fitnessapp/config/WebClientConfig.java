package com.fitness.fitnessapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${services.python-ai.url}")
    private String pythonServiceUrl;

    @Value("${services.go-stats.url}")
    private String goServiceUrl;

    @Bean
    public WebClient pythonWebClient(){
        return WebClient.builder().baseUrl(pythonServiceUrl).build();
    }

    @Bean
    public WebClient goWebClient(){
        return WebClient.builder().baseUrl(goServiceUrl).build();
    }
}
