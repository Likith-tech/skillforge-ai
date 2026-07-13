package com.skillforge.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pure unit tests for JWT generation/validation - no Spring context needed,
 * secret/expiration are injected directly via ReflectionTestUtils the way
 * Spring would via @Value.
 */
class JwtUtilTest {

    private static final String TEST_SECRET =
            "dGVzdC1vbmx5LWp3dC1zaWduaW5nLWtleS1uZXZlci11c2VkLW91dHNpZGUtdGVzdHM=";

    private JwtUtil jwtUtil;
    private UserDetails student;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3600_000L);

        student = User.withUsername("student@example.com")
                .password("irrelevant")
                .authorities(List.of(() -> "ROLE_STUDENT"))
                .build();
    }

    @Test
    void generateToken_roundTripsUsernameAndRoles() {
        String token = jwtUtil.generateToken(student);

        assertThat(jwtUtil.extractUsername(token)).isEqualTo("student@example.com");
        assertThat(jwtUtil.extractRoles(token)).containsExactly("ROLE_STUDENT");
    }

    @Test
    void isTokenValid_trueForMatchingUser() {
        String token = jwtUtil.generateToken(student);

        assertThat(jwtUtil.isTokenValid(token, student)).isTrue();
    }

    @Test
    void isTokenValid_falseWhenUsernameDoesNotMatch() {
        String token = jwtUtil.generateToken(student);
        UserDetails someoneElse = User.withUsername("recruiter@example.com")
                .password("irrelevant")
                .authorities(List.of(() -> "ROLE_RECRUITER"))
                .build();

        assertThat(jwtUtil.isTokenValid(token, someoneElse)).isFalse();
    }

    @Test
    void expiredToken_throwsOnParse() {
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", -60_000L); // already expired
        String expiredToken = jwtUtil.generateToken(student);

        assertThatThrownBy(() -> jwtUtil.extractUsername(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
        assertThatThrownBy(() -> jwtUtil.isTokenValid(expiredToken, student))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void tamperedToken_throwsOnParse() {
        String token = jwtUtil.generateToken(student);
        // Flip the last character of the signature segment.
        String tampered = token.substring(0, token.length() - 1)
                + (token.charAt(token.length() - 1) == 'A' ? 'B' : 'A');

        assertThatThrownBy(() -> jwtUtil.extractUsername(tampered))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void malformedToken_throwsOnParse() {
        assertThatThrownBy(() -> jwtUtil.extractUsername("not-a-jwt-at-all"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void getExpirationMs_returnsConfiguredValue() {
        assertThat(jwtUtil.getExpirationMs()).isEqualTo(3600_000L);
    }
}
