package com.restaurant.order_service.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenValidatorTest {

    private static final String SECRET = "bWluaGEtY2hhdmUtc3VwZXItc2VjcmV0YS1jb20tbWFpcy1kZS0zMi1jYXJhY3RlcmVz";
    private static final String WRONG_SECRET = "d3Jvbmcta2V5LWZvci10ZXN0LXdpdGgtMzItcGx1cy1jaGFycy1wYWRkaW5n";

    private SecretKey key;
    private JwtTokenValidator validator;

    @BeforeEach
    void setUp() {
        key = new SecretKeySpec(Decoders.BASE64.decode(SECRET), "HmacSHA256");
        validator = new JwtTokenValidator(SECRET);
    }

    private String generateToken(String subject, Map<String, Object> claims, SecretKey signingKey) {
        var builder = Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000));
        claims.forEach(builder::claim);
        return builder.signWith(signingKey).compact();
    }

    @Test
    @DisplayName("extracts userId and role when both claims are present")
    void extractsAllClaimsWhenPresent() {
        String token = generateToken(
                "pedro@example.com",
                Map.of("userId", 42L, "userRole", "CLIENT"),
                key
        );

        AuthenticatedClient client = validator.validateAndExtract(token);

        assertThat(client.email()).isEqualTo("pedro@example.com");
        assertThat(client.clientId()).isEqualTo(42L);
        assertThat(client.role()).isEqualTo("CLIENT");
    }

    @Test
    @DisplayName("uppercases the role for consistency")
    void uppercasesRole() {
        String token = generateToken(
                "pedro@example.com",
                Map.of("userId", 1L, "userRole", "client"),
                key
        );

        assertThat(validator.validateAndExtract(token).role()).isEqualTo("CLIENT");
    }

    @Test
    @DisplayName("falls back to a stable id derived from email when userId claim is missing")
    void fallsBackOnMissingUserId() {
        String token = generateToken(
                "pedro@example.com",
                Map.of("userRole", "CLIENT"),
                key
        );

        AuthenticatedClient first = validator.validateAndExtract(token);
        AuthenticatedClient second = validator.validateAndExtract(token);

        assertThat(first.clientId()).isPositive();
        assertThat(first.clientId()).isEqualTo(second.clientId());
    }

    @Test
    @DisplayName("falls back to CLIENT when role claim is missing")
    void fallsBackOnMissingRole() {
        String token = generateToken(
                "pedro@example.com",
                Map.of("userId", 1L),
                key
        );

        assertThat(validator.validateAndExtract(token).role()).isEqualTo("CLIENT");
    }

    @Test
    @DisplayName("handles the current auth-service token format (sub only, no extra claims)")
    void worksWithCurrentAuthServiceTokens() {
        String token = generateToken("pedro@example.com", Map.of(), key);

        AuthenticatedClient client = validator.validateAndExtract(token);

        assertThat(client.email()).isEqualTo("pedro@example.com");
        assertThat(client.clientId()).isPositive();
        assertThat(client.role()).isEqualTo("CLIENT");
    }

    @Test
    @DisplayName("rejects token signed with a different secret")
    void rejectsWrongSignature() {
        SecretKey wrong = new SecretKeySpec(Decoders.BASE64.decode(WRONG_SECRET), "HmacSHA256");
        String token = generateToken("x", Map.of("userId", 1L, "userRole", "CLIENT"), wrong);

        assertThatThrownBy(() -> validator.validateAndExtract(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("rejects malformed token")
    void rejectsMalformed() {
        assertThatThrownBy(() -> validator.validateAndExtract("not-a-jwt"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("rejects expired token")
    void rejectsExpired() {
        String expired = Jwts.builder()
                .subject("x")
                .issuedAt(new Date(System.currentTimeMillis() - 120_000))
                .expiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(key)
                .compact();

        assertThatThrownBy(() -> validator.validateAndExtract(expired))
                .isInstanceOf(JwtException.class);
    }
}
