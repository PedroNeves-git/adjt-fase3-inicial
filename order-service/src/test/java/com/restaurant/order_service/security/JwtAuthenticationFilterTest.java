package com.restaurant.order_service.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private JwtTokenValidator validator;
    private JwtAuthenticationFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        validator = mock(JwtTokenValidator.class);
        filter = new JwtAuthenticationFilter(validator);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("does nothing and forwards when no Authorization header is present")
    void noHeaderPasses() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("ignores non-Bearer Authorization headers")
    void nonBearerIsIgnored() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic abc==");

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("populates SecurityContext when token is valid")
    void validTokenPopulatesContext() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer some-valid-token");
        when(validator.validateAndExtract("some-valid-token"))
                .thenReturn(new AuthenticatedClient(42L, "pedro@example.com", "CLIENT"));

        filter.doFilter(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isInstanceOf(AuthenticatedClient.class);
        AuthenticatedClient client = (AuthenticatedClient) auth.getPrincipal();
        assertThat(client.clientId()).isEqualTo(42L);
        assertThat(auth.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("does not populate context when validator rejects token, but still forwards")
    void invalidTokenIsTolerated() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer broken");
        when(validator.validateAndExtract(any())).thenThrow(new JwtException("bad signature"));

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain, times(1)).doFilter(request, response);
    }
}
