package com.dcm.demo.service.impl;

import com.dcm.demo.service.interfaces.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access_ttl}")
    private int accessTokenTtl;
    @Override
    public String generate(int id, String role, int expiration) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expiration, ChronoUnit.MINUTES);
        return Jwts.builder()
                .setSubject(id + "")
                .setIssuer("TIEN-DEV-JAVA")
                .setAudience("localhost:8080")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("role", role)
                .signWith(encodeSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String refresh(String refreshToken) {
        String subject = getClaims(refreshToken).getSubject();
        String role = getClaims(refreshToken).get("role", String.class);
        return generate(999, "OK", accessTokenTtl);
    }

    @Override
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(encodeSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public SecretKey encodeSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
