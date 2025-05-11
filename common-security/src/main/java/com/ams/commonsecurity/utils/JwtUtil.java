package com.ams.commonsecurity.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * {@code JwtUtil} is a utility class for handling JSON Web Tokens (JWT).
 *
 * <p>This class provides core JWT functionality such as:</p>
 * <ul>
 *   <li>Generating signed JWT tokens for authenticated users</li>
 *   <li>Extracting user identity (subject) from a token</li>
 *   <li>Validating the authenticity and structure of JWT tokens</li>
 * </ul>
 *
 * <p>
 * Tokens are signed using the HS256 algorithm with a Base64-encoded symmetric secret key.
 * </p>
 *
 * <p><b>Usage Context:</b> Used by multiple microservices (e.g., Gateway, User-Service, Client-Service)
 * for secure authentication and authorization flows.</p>
 *
 * <p><b>Security Advice:</b> In production environments, always store the secret key securely
 * (e.g., using environment variables or secrets vaults), and never hardcode it.</p>
 *
 * @author Yosef Nago
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    /**
     * Initializes the {@code JwtUtil} with a base64-encoded secret key.
     *
     * @param base64Secret the secret key encoded as a Base64 string, injected from application properties
     */
    public JwtUtil(@Value("${jwt.secret}") String base64Secret) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    }

    /**
     * Extracts the username (i.e., subject) from a JWT token.
     *
     * @param token the JWT string to parse
     * @return the username embedded within the token
     * @throws JwtException if the token is invalid, malformed, or has an invalid signature
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Generates a signed JWT token for a given username.
     *
     * <p>The token is valid for 10 hours from issuance.</p>
     *
     * @param username the subject to be embedded in the token
     * @return a compact, signed JWT string
     */
    public String generateToken(String username,String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role",role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates a JWT token.
     *
     * <p>Checks the structure, signature, and expiration date of the token.</p>
     *
     * @param token the token to validate
     * @return {@code true} if the token is valid, {@code false} if expired or invalid
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    public String extractRole(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role",String.class);
    }

}
