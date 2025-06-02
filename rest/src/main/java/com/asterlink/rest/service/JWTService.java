package com.asterlink.rest.service;

import com.asterlink.rest.model.Account;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

/**
 * JWTService interface.
 * Code for necessary token processing logic.
 * @author gl3bert
 */

public interface JWTService {

    // Token generation.
    String generateToken(String email);

    // Validate token.
    boolean validateToken(String token, UserDetails userDetails);

    // Extract email from token.
    String extractUsername(String token);

    // Extract expiration from token.
    Date extractExpiration(String token);

    // Extract claim from token.
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

}
