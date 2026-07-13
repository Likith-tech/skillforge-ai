package com.skillforge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillforge.AbstractIntegrationTest;
import com.skillforge.dto.AuthResponse;
import com.skillforge.dto.LoginRequest;
import com.skillforge.dto.RegisterRequest;
import com.skillforge.dto.UserProfileResponse;
import com.skillforge.exception.DuplicateResourceException;
import com.skillforge.service.AuthService;
import com.skillforge.support.WithMockSecurityUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-context controller test: real SecurityConfig/JwtAuthenticationFilter/
 * AuthRateLimitingFilter chain is active (Testcontainers Postgres backs
 * CustomUserDetailsService), only AuthService's business logic is mocked.
 * Verifies HTTP status codes, JSON shape, and validation - not business rules
 * (those are AuthServiceImplTest's job).
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AuthService authService;
    @MockBean private UserDetailsService userDetailsService; // unused directly; satisfies JwtAuthenticationFilter's dependency without touching the DB

    @Test
    void register_valid_returns201WithToken() throws Exception {
        RegisterRequest request = new RegisterRequest("Asha Rao", "asha@example.com", "Password123", null);
        AuthResponse response = AuthResponse.builder().token("jwt-token").tokenType("Bearer").userId(1L)
                .fullName("Asha Rao").email("asha@example.com").roles(Set.of("STUDENT")).expiresInMs(86_400_000L).build();
        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.roles[0]").value("STUDENT"));
    }

    @Test
    void register_blankFullName_returns400WithFieldError() throws Exception {
        RegisterRequest request = new RegisterRequest("", "asha@example.com", "Password123", null);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.fullName").exists());
    }

    @Test
    void register_passwordTooShort_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest("Asha Rao", "asha@example.com", "short", null);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void register_invalidEmail_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest("Asha Rao", "not-an-email", "Password123", null);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        RegisterRequest request = new RegisterRequest("Asha Rao", "asha@example.com", "Password123", null);
        when(authService.register(any())).thenThrow(new DuplicateResourceException("An account with this email already exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void login_valid_returns200WithToken() throws Exception {
        LoginRequest request = new LoginRequest("asha@example.com", "Password123");
        AuthResponse response = AuthResponse.builder().token("jwt-token").tokenType("Bearer").userId(1L)
                .fullName("Asha Rao").email("asha@example.com").roles(Set.of("STUDENT")).expiresInMs(86_400_000L).build();
        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_badCredentials_returns401() throws Exception {
        LoginRequest request = new LoginRequest("asha@example.com", "wrong");
        when(authService.login(any())).thenThrow(
                new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void profile_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/auth/profile")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockSecurityUser(id = 1L, email = "asha@example.com", role = "STUDENT")
    void profile_authenticated_returns200WithProfile() throws Exception {
        UserProfileResponse profile = UserProfileResponse.builder().id(1L).fullName("Asha Rao")
                .email("asha@example.com").roles(Set.of("STUDENT")).enabled(true).createdAt(LocalDateTime.now()).build();
        when(authService.getProfile("asha@example.com")).thenReturn(profile);

        mockMvc.perform(get("/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("asha@example.com"));
    }
}
