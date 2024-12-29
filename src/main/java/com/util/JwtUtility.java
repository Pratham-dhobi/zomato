package com.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtility {

    private static final String SECRET_KEY = "mA3l7T7ULOuppxmLnDUl3WkGfVYOahN0";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hours

    private final Key key;

    public JwtUtility() {
        // Generate a signing key from the secret string
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

//    generate token with email and role
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    
//     Validate the token and check its expiration.
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    
//    Extract email from the token.    
    public String validateTokeAndGetEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }


//     Extract role from the token.
    public String validateTokenAndGetRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }


//    Extract all claims from the token.
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}

