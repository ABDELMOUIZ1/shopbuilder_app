package com.wordpress.shopBuilder.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtConfig {


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Long userId, String email,String domaineName, String firstName, String lastName) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(Long.toString(userId))
//                 .claim("role", role)
                .claim("domaineName", domaineName)
                .claim("email", email)
                .claim("firstName", firstName)
                .claim("lastName", lastName)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Long extractUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException ex) {
            System.err.println("JWT token expired");
        } catch (UnsupportedJwtException ex) {
            System.err.println("JWT token unsupported");
        } catch (MalformedJwtException ex) {
            System.err.println("JWT token malformed");
        } catch (SignatureException ex) {
            System.err.println("JWT signature invalid");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
