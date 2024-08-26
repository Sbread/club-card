package com.t1project.club_card.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class Utils {
    private Utils() {
    }

    public static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static String extractBearerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}
