package com.restaurant.order_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Service
public class JwtTokenValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenValidator.class);

    private final SecretKey key;

    public JwtTokenValidator(@Value("${security.jwt.secret}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public AuthenticatedClient validateAndExtract(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String email = claims.getSubject();
        Long clientId = resolveClientId(claims, email);
        String role = resolveRole(claims);

        return new AuthenticatedClient(clientId, email, role);
    }

    private Long resolveClientId(Claims claims, String email) {
        Object raw = claims.get("userId");
        if (raw == null) {
            return stableClientIdFromEmail(email);
        }
        if (raw instanceof Number n) {
            return n.longValue();
        }
        return Long.parseLong(raw.toString());
    }

    private String resolveRole(Claims claims) {
        Object raw = claims.get("userRole");
        if (raw == null) {
            return "CLIENT";
        }
        return raw.toString().trim().toUpperCase();
    }

    private static long stableClientIdFromEmail(String email) {
        if (email == null) {
            return 0L;
        }
        long h = 1125899906842597L;
        for (int i = 0; i < email.length(); i++) {
            h = 31 * h + email.charAt(i);
        }
        return Math.abs(h);
    }
}
