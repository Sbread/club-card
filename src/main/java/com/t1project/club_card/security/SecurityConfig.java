package com.t1project.club_card.security;

import com.t1project.club_card.configuration_properties.CorsConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private CustomReactiveAuthenticationManager customReactiveAuthenticationManager;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         CorsConfigurationProperties corsConfigurationProperties) {
        return http
                //.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(configure -> {
                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    corsConfigurationProperties.getConfigurations().forEach(source::registerCorsConfiguration);
                    configure.configurationSource(source);
                })
                .authorizeExchange(
                        exchange -> exchange.pathMatchers("/",
                                        "/api/**",
                                        "/login",
                                        "/register",
                                        "/refreshToken",
                                        "/webjars/swagger-ui/index.html")
                                .permitAll()
                                .anyExchange()
                                .authenticated())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .addFilterBefore(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authenticationManager(customReactiveAuthenticationManager)
                .build();
    }
}
