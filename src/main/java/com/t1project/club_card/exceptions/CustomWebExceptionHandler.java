package com.t1project.club_card.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomWebExceptionHandler implements WebExceptionHandler {

    public CustomWebExceptionHandler() {
        System.out.println("CustomWebExceptionHandler initialized");
    }

    @NonNull
    @Override
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        HttpStatus status;
        String message;
        switch (ex) {
            case ExpiredJwtException expiredJwtException -> {
                status = HttpStatus.UNAUTHORIZED;
                message = "JWT token has expired";
            }
            case JwtException jwtException -> {
                status = HttpStatus.UNAUTHORIZED;
                message = "Invalid JWT token";
            }
            case InvalidAccessTokenException invalidAccessTokenException -> {
                status = HttpStatus.UNAUTHORIZED;
                message = "Invalid access token";
            }
            case RefreshTokenExpiredException refreshTokenExpiredException -> {
                status = HttpStatus.FORBIDDEN;
                message = "Refresh token has expired";
            }
            case AccessDeniedException accessRightsException -> {
                status = HttpStatus.FORBIDDEN;
                message = accessRightsException.getMessage();
            }
            case UsernameNotFoundException usernameNotFoundException -> {
                status = HttpStatus.FORBIDDEN;
                message = usernameNotFoundException.getMessage();
            }
            default -> {
                System.out.println("UNHANDLED EXCEPTION " + ex);
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "unhandled exception";
            }
        }
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String responseBody = "{\"message\": \"" + message + "\"}";
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        DataBuffer dataBuffer = dataBufferFactory.wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }
}