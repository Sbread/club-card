package com.t1project.club_card.configuration_properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@ConfigurationProperties(prefix = "spring.cors.url")
public class CorsConfigurationProperties {

    private Map<String, CorsConfiguration> configurations = new LinkedHashMap<>();

    public void setConfigurations(LinkedHashMap<String, CorsConfiguration> configurations) {
        this.configurations = configurations;
    }
}
