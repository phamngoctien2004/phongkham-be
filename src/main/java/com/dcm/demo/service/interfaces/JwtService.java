package com.dcm.demo.service.interfaces;

import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;

public interface JwtService {
    String generate(int id, String role, int expiration);
    String refresh(String refreshToken);
    Claims getClaims(String token);
    SecretKey encodeSecretKey();
}
