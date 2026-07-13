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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private static final Set<RoleName> SELF_REGISTERABLE_ROLES = Set.of(RoleName.STUDENT, RoleName.RECRUITER);

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        RoleName roleName = resolveSelfRegisterableRole(request.getRole());
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(new HashSet<>(Set.of(role)))
                .build();

        userRepository.save(user);

        SecurityUser securityUser = new SecurityUser(user);
        String token = jwtUtil.generateToken(securityUser);

        return buildAuthResponse(user, token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String token = jwtUtil.generateToken(securityUser);

        return buildAuthResponse(securityUser.getUser(), token);
    }

    @Override
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No user found with email: " + email));

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roles(roleNames(user))
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private RoleName resolveSelfRegisterableRole(String requestedRole) {
        if (requestedRole == null || requestedRole.isBlank()) {
            return RoleName.STUDENT;
        }

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(requestedRole.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidRoleException("Unknown role: " + requestedRole);
        }

        if (!SELF_REGISTERABLE_ROLES.contains(roleName)) {
            throw new InvalidRoleException("Role " + roleName + " cannot be assigned through self-registration");
        }

        return roleName;
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roles(roleNames(user))
                .expiresInMs(jwtUtil.getExpirationMs())
                .build();
    }

    private Set<String> roleNames(User user) {
        return user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}
