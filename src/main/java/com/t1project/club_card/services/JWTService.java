package com.t1project.club_card.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTService {
    public static final String SECRET = "56DW526FEF54Q4D8FEW445D8WD78DD8QWF8QWF48W4F8W";
    public static final int TOKEN_LIVING_TIME = 900000;

    @Autowired
    private ClubMemberService clubMemberService;

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String extractPrivilege(String token) {
        return extractClaim(token, claims -> claims.get("privilege", String.class));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Mono<String> GenerateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }


    private Mono<String> createToken(Map<String, Object> claims, String email) {
        return clubMemberService.findByEmail(email).map(
                clubMember -> {
                    claims.put("role", clubMember.getRole());
                    claims.put("privilege", clubMember.getPrivilege());
                    return Jwts.builder()
                            .claims(claims)
                            .subject(clubMember.getEmail())
                            .issuedAt(new Date(System.currentTimeMillis()))
                            .expiration(new Date(System.currentTimeMillis() + TOKEN_LIVING_TIME))
                            .signWith(getSecretKey(), Jwts.SIG.HS256).compact();
                }
        );
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
