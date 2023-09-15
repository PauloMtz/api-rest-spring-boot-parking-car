package com.parking.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtils {
    
    public static final String JWT_BEARER = "Bearer ";
    public static final String JWT_AUTHORIZATION = "Authorization";
    // a SECRET_KEY tem que ter no mínimo 32 caracteres
    public static final String SECRET_KEY = "ad|fhkl/jh238470.970HK%HKJ7@98*9_35";
    public static final long EXPIRE_DAYS = 0;
    public static final long EXPIRE_HOURS = 0;
    public static final long EXPIRE_MINUTES = 20;

    public JwtUtils() {}

    public static boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(generateKey()).build()
                .parseClaimsJws(refactorToken(token));

            return true;
        } catch (JwtException ex) {
            log.error(String.format("Token inválido %s ", ex.getMessage()));
        }

        return false;
    } 

    private static Key generateKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    private static Date dataExpiracao(Date inicio) {
        LocalDateTime dateTime = inicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime fim = dateTime.plusDays(EXPIRE_DAYS).plusHours(EXPIRE_HOURS).plusMinutes(EXPIRE_MINUTES);
        return Date.from(fim.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static JwtToken createToken(String username, String role) {
        Date issuedAt = new Date();
        Date limit = dataExpiracao(issuedAt);
        String token = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setSubject(username)
            .setIssuedAt(issuedAt)
            .setExpiration(limit)
            .signWith(generateKey(), SignatureAlgorithm.HS256)
            .claim("role", role)
            .compact();

        return new JwtToken(token);
    }

    public static String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    private static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(generateKey()).build()
                .parseClaimsJws(refactorToken(token)).getBody();
        } catch (JwtException ex) {
            log.error(String.format("Token inválido %s ", ex.getMessage()));
        }

        return null;
    }

    private static String refactorToken(String token) {
        if (token.contains(JWT_BEARER)) {
            return token.substring(JWT_BEARER.length());
        }

        return token;
    }
}
