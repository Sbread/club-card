package com.t1project.club_card.database;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@Configuration
public class DbConxInit {
    @Bean
    public ConnectionFactoryInitializer initializer(@Qualifier("connectionFactory")
                                                    ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer =
                new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        ResourceDatabasePopulator resource =
                new ResourceDatabasePopulator(new ClassPathResource("init_base.sql"));
        initializer.setDatabasePopulator(resource);
        return initializer;
    }
}
