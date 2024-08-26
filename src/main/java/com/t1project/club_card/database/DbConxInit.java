package com.t1project.club_card.database;

import com.t1project.club_card.models.ClubMember;
import com.t1project.club_card.repositories.ClubMemberRepository;
import com.t1project.club_card.utils.Utils;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.util.HashSet;
import java.util.Set;

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

    @Bean
    public CommandLineRunner init(ClubMemberRepository clubMemberRepository) {
        return args -> {
            final Set<String> roles = new HashSet<>();
            roles.add("ROLE_SUPERUSER");
            ClubMember superuser = ClubMember.builder()
                    .username("superuser")
                    .password(Utils.bCryptPasswordEncoder.encode("supPas2608"))
                    .email("superuser@yandex.ru")
                    .firstName("sup")
                    .lastName("sup")
                    .phoneNumber(null)
                    .privilege("VIP")
                    .isLocked(false)
                    .roles(roles)
                    .build();
            clubMemberRepository.save(superuser)
                    .onErrorResume(e -> clubMemberRepository.findMemberByUsername("superuser"))
                    .thenMany(clubMemberRepository.findAll())
                    .subscribe(System.out::println);
        };
    }
}
