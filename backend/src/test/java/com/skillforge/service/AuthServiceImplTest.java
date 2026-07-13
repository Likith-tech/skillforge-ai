package com.skillforge.service;

import com.skillforge.dto.AuthResponse;
import com.skillforge.dto.LoginRequest;
import com.skillforge.dto.RegisterRequest;
import com.skillforge.dto.UserProfileResponse;
import com.skillforge.exception.DuplicateResourceException;
import com.skillforge.exception.InvalidRoleException;
import com.skillforge.exception.ResourceNotFoundException;
import com.skillforge.model.Role;
import com.skillforge.model.RoleName;
import com.skillforge.model.User;
import com.skillforge.repository.RoleRepository;
import com.skillforge.repository.UserRepository;
import com.skillforge.security.SecurityUser;
import com.skillforge.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Business-logic tests for AuthServiceImpl with every repository/collaborator mocked. */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, roleRepository, passwordEncoder, authenticationManager, jwtUtil);
    }

    private Role role(RoleName name) {
        return new Role(name);
    }

    @Test
    void register_createsStudentByDefaultAndReturnsToken() {
        RegisterRequest request = new RegisterRequest("Asha Rao", "asha@example.com", "Password123", null);
        when(userRepository.existsByEmail("asha@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.STUDENT)).thenReturn(Optional.of(role(RoleName.STUDENT)));
        when(passwordEncoder.encode("Password123")).thenReturn("hashed");
        when(jwtUtil.generateToken(any())).thenReturn("token-123");
        when(jwtUtil.getExpirationMs()).thenReturn(86_400_000L);

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("token-123");
        assertThat(response.getRoles()).containsExactly("STUDENT");

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUser.capture());
        assertThat(savedUser.getValue().getPassword()).isEqualTo("hashed");
        assertThat(savedUser.getValue().getEmail()).isEqualTo("asha@example.com");
    }

    @Test
    void register_recruiterRole_isSelfRegisterable() {
        RegisterRequest request = new RegisterRequest("Neel Kapoor", "neel@example.com", "Password123", "recruiter");
        when(userRepository.existsByEmail("neel@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.RECRUITER)).thenReturn(Optional.of(role(RoleName.RECRUITER)));
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(jwtUtil.generateToken(any())).thenReturn("token");

        AuthResponse response = authService.register(request);

        assertThat(response.getRoles()).containsExactly("RECRUITER");
    }

    @Test
    void register_adminRole_rejectedAsNotSelfRegisterable() {
        RegisterRequest request = new RegisterRequest("Wannabe Admin", "admin2@example.com", "Password123", "ADMIN");
        when(userRepository.existsByEmail("admin2@example.com")).thenReturn(false);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(InvalidRoleException.class)
                .hasMessageContaining("cannot be assigned");
    }

    @Test
    void register_unknownRoleString_throwsInvalidRoleException() {
        RegisterRequest request = new RegisterRequest("X", "x@example.com", "Password123", "SUPERUSER");
        when(userRepository.existsByEmail("x@example.com")).thenReturn(false);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(InvalidRoleException.class);
    }

    @Test
    void register_duplicateEmail_throwsDuplicateResourceException() {
        RegisterRequest request = new RegisterRequest("Dup", "dup@example.com", "Password123", null);
        when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void login_delegatesToAuthenticationManagerAndReturnsToken() {
        LoginRequest request = new LoginRequest("asha@example.com", "Password123");
        User user = User.builder().id(1L).fullName("Asha Rao").email("asha@example.com")
                .roles(Set.of(role(RoleName.STUDENT))).build();
        SecurityUser principal = new SecurityUser(user);
        Authentication authResult = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authResult);
        when(jwtUtil.generateToken(principal)).thenReturn("token-456");
        when(jwtUtil.getExpirationMs()).thenReturn(86_400_000L);

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("token-456");
        assertThat(response.getUserId()).isEqualTo(1L);
    }

    @Test
    void login_badCredentials_propagatesAuthenticationException() {
        LoginRequest request = new LoginRequest("asha@example.com", "wrong-password");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request)).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void getProfile_returnsMappedProfile() {
        User user = User.builder().id(7L).fullName("Priya Sharma").email("priya@example.com")
                .roles(Set.of(role(RoleName.STUDENT))).enabled(true).build();
        when(userRepository.findByEmail("priya@example.com")).thenReturn(Optional.of(user));

        UserProfileResponse profile = authService.getProfile("priya@example.com");

        assertThat(profile.getId()).isEqualTo(7L);
        assertThat(profile.getRoles()).containsExactly("STUDENT");
        assertThat(profile.isEnabled()).isTrue();
    }

    @Test
    void getProfile_unknownEmail_throwsResourceNotFoundException() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getProfile("ghost@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
