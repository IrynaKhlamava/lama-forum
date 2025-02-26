package com.company.service;

public interface JwtService {

    String generateToken(String email);

    boolean validateToken(String token);

    String extractEmail(String token);

}
