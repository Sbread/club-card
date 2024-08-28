package com.t1project.club_card.security;

import com.t1project.club_card.exceptions.JwtTokenExpiredException;
import com.t1project.club_card.repositories.BlacklistTokenRepository;
import com.t1project.club_card.services.BlacklistTokenService;
import com.t1project.club_card.services.ClubMemberUserDetailsService;
import com.t1project.club_card.services.JWTService;
import com.t1project.club_card.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements WebFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ClubMemberUserDetailsService clubMemberUserDetailsService;

    @Autowired
    private BlacklistTokenService blacklistTokenRepositoryService;

    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = Utils.extractBearerToken(authHeader);
        String username = token == null ? null : jwtService.extractUsername(token);
        if (username != null) {
            return blacklistTokenRepositoryService.existsByToken(token)
                    .flatMap(isBlacklisted -> {
                        if (isBlacklisted) {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        } else {
                            return clubMemberUserDetailsService.findByUsername(username)
                                    .flatMap(userDetails -> {
                                        if (jwtService.validateToken(token, userDetails)) {
                                            UsernamePasswordAuthenticationToken authenticationToken
                                                    = new UsernamePasswordAuthenticationToken(
                                                    userDetails, null, userDetails.getAuthorities());
                                            SecurityContext securityContext = new SecurityContextImpl(authenticationToken);
                                            return chain.filter(exchange).contextWrite(
                                                    ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                                        } else {
                                            throw new JwtTokenExpiredException("Jwt access token expired");
                                        }
                                    })
                                    .switchIfEmpty(chain.filter(exchange));
                        }
                    });
        }
        return chain.filter(exchange);
    }
}
