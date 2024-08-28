package com.t1project.club_card;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableR2dbcRepositories
public class ClubCardApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClubCardApplication.class, args);
    }

}
