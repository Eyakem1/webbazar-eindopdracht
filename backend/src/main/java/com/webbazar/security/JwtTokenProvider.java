package com.webbazar.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Base64;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

@Component
public class JwtTokenProvider {

    private String secretKey = "mySuperSecretKey12345mySuperSecretKey12345"; // moet minimaal 256 bits zijn
    private final long validityInMilliseconds = 3600000; // 1 uur
    private SecretKey key;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(secretKey.getBytes()));
    }

    // ✅ Aangepast voor meerdere rollen
    public String createToken(String email, Set<String> roles) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles); // ⬅️ gebruik meerdere rollen

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ✅ Haal meerdere rollen op uit token
    public Set<String> getRolesFromToken(String token) {
        Object raw = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles");

        if (raw instanceof List<?>) {
            List<?> list = (List<?>) raw;
            Set<String> roles = new HashSet<>();
            for (Object obj : list) {
                roles.add(obj.toString());
            }
            return roles;
        }

        return Set.of(); // fallback
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
