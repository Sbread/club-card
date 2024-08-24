package com.t1project.club_card;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecureConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login", "/refreshToken").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests(authorize -> authorize
//                .requestMatchers("/login", "/register").permitAll()
//                .requestMatchers("/profile").permitAll()
//                .requestMatchers("/club_admin/**").hasAnyRole("ROLE_ADMIN", "ROLE_SUPERUSER")
//                .anyRequest().authenticated()
//        ).formLogin(form -> form
//                .loginPage("/login")
//                .defaultSuccessUrl("home", true) //maybe fix
//        ).logout(logout -> logout
//                .logoutSuccessUrl("/login"));
//
//        return http.build();
//    }

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
