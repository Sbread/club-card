package com.t1project.club_card.database;

import com.t1project.club_card.models.ClubMember;
import com.t1project.club_card.models.TemplatePrivilege;
import com.t1project.club_card.repositories.ClubMemberRepository;
import com.t1project.club_card.repositories.TemplatePrivilegesRepository;
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
import java.util.List;
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
    public CommandLineRunner init(ClubMemberRepository clubMemberRepository,
                                  TemplatePrivilegesRepository templatePrivilegesRepository) {
        return args -> {
            ClubMember superuser = ClubMember.builder()
                    .email("superuser@yandex.ru")
                    .password(Utils.bCryptPasswordEncoder.encode("supPas2608"))
                    .phoneNumber("+7999999999")
                    .firstName("sup")
                    .lastName("sup")
                    .birthday(null)
                    .privilege("VIP")
                    .isLocked(false)
                    .role("ROLE_SUPERUSER")
                    .template(null)
                    .build();
            clubMemberRepository.save(superuser)
                    .onErrorResume(e -> clubMemberRepository.findByEmail("superuser@yandex.ru"))
                    .thenMany(clubMemberRepository.findAll())
                    .subscribe(System.out::println);
            Set<String> tSet1 = new HashSet<>(List.of("ROLE_USER"));
            Set<String> tSet2 = new HashSet<>(List.of("ROLE_USER", "ROLE_ADMIN"));
            Set<String> tSet3 = new HashSet<>(List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPERUSER"));
            TemplatePrivilege templatePrivilege1 = TemplatePrivilege.builder().template("1").privileges(tSet1).build();
            TemplatePrivilege templatePrivilege2 = TemplatePrivilege.builder().template("2").privileges(tSet2).build();
            TemplatePrivilege templatePrivilege3 = TemplatePrivilege.builder().template("3").privileges(tSet3).build();
            templatePrivilegesRepository.saveAll(List.of(templatePrivilege1, templatePrivilege2, templatePrivilege3));
        };
    }
}
