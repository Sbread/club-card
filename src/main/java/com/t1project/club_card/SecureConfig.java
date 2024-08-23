package com.t1project.club_card;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecureConfig {
    private final PasswordEncoder pwEncoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/register").permitAll()
                .requestMatchers("/profile").permitAll()
                .requestMatchers("/club_admin/**").hasAnyRole("ROLE_ADMIN", "ROLE_SUPERUSER")
                .anyRequest().authenticated()
        ).formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("home", true) //maybe fix
        ).logout(logout -> logout
                .logoutSuccessUrl("/login"));

        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService(ClubMemberRepository clubMemberRepository)
//            throws UsernameNotFoundException {
//        return username -> {
//            ClubMember clubMember = clubMemberRepository.findMemberByUsername(username);
//            if (clubMember == null) {
//                throw new UsernameNotFoundException("User with username: " + username + " not found");
//            }
//            return new org.springframework.security.core.userdetails.User(clubMember.getUsername(),
//                    clubMember.getPassword(),
//                    AuthorityUtils.createAuthorityList(clubMember.getRole()));
//        };
//    }
}
