package com.t1project.club_card.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class Utils {
    private Utils() {}

    public static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
}
