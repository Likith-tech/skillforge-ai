package com.skillforge.security.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillforge.exception.ApiError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Throttles brute-force/credential-stuffing attempts against the unauthenticated
 * auth endpoints. Runs ahead of JwtAuthenticationFilter so a flood of requests
 * never even reaches token parsing or the AuthenticationManager.
 */
@Component
@RequiredArgsConstructor
public class AuthRateLimitingFilter extends OncePerRequestFilter {

    /** requestURI -> max requests per WINDOW, per client key. */
    private static final Map<String, Integer> LIMITED_PATHS = Map.of(
            "/auth/login", 10,
            "/auth/register", 5
    );
    private static final Duration WINDOW = Duration.ofMinutes(1);

    private final InMemoryRateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        Integer limit = LIMITED_PATHS.get(request.getRequestURI());
        if (limit == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = clientKey(request) + ":" + request.getRequestURI();
        if (!rateLimiter.tryConsume(key, limit, WINDOW)) {
            writeTooManyRequests(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String clientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeTooManyRequests(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiError body = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error("Too Many Requests")
                .message("Too many attempts. Please try again in a minute.")
                .path(request.getRequestURI())
                .build();

        objectMapper.writeValue(response.getWriter(), body);
    }
}
