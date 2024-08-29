package com.t1project.club_card.security;

import com.t1project.club_card.exceptions.InvalidAccessTokenException;
import com.t1project.club_card.services.BlacklistTokenService;
import com.t1project.club_card.services.ClubMemberUserDetailsService;
import com.t1project.club_card.services.JWTService;
import com.t1project.club_card.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

    @NonNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = Utils.extractBearerToken(authHeader);
        if (token != null) {
            return Mono.just(token)
                    .map(jwtService::extractUsername)
                    .flatMap(username -> blacklistTokenRepositoryService.existsByToken(token)
                            .flatMap(isBlacklisted -> {
                                if (isBlacklisted) {
                                    return Mono.error(new InvalidAccessTokenException("Invalid access token"));
                                } else {
                                    return clubMemberUserDetailsService.findByUsername(username)
                                            .flatMap(userDetails -> {
                                                        UsernamePasswordAuthenticationToken authenticationToken
                                                                = new UsernamePasswordAuthenticationToken(
                                                                userDetails, null, userDetails.getAuthorities());
                                                        SecurityContext securityContext = new SecurityContextImpl(authenticationToken);
                                                        return chain.filter(exchange).contextWrite(
                                                                ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                                                    }
                                            )
                                            .switchIfEmpty(chain.filter(exchange));
                                }
                            }))
                    .onErrorResume(Mono::error);
        }
        return chain.filter(exchange);
    }
}
