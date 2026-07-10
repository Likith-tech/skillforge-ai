package com.skillforge.service;

import com.skillforge.dto.ChangePasswordRequest;
import com.skillforge.dto.UserSettingsRequest;
import com.skillforge.dto.UserSettingsResponse;
import com.skillforge.model.User;
import com.skillforge.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserAccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserSettingsResponse getSettings(Long userId) {
        return UserSettingsResponse.from(findUser(userId));
    }

    public UserSettingsResponse updateSettings(Long userId, UserSettingsRequest request) {
        User user = findUser(userId);
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        userRepository.findByEmail(normalizedEmail)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
                });

        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setEmailNotificationsEnabled(valueOrDefault(request.getEmailNotificationsEnabled(), true));
        user.setInAppNotificationsEnabled(valueOrDefault(request.getInAppNotificationsEnabled(), true));
        user.setThemePreference(normalizeTheme(request.getThemePreference()));

        try {
            return UserSettingsResponse.from(userRepository.save(user));
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password and confirm password must match");
        }

        User user = findUser(userId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Boolean valueOrDefault(Boolean value, boolean defaultValue) {
        return value == null ? defaultValue : value;
    }

    private String normalizeTheme(String themePreference) {
        if (themePreference == null || themePreference.trim().isEmpty()) {
            return "SYSTEM";
        }
        return themePreference.trim().toUpperCase();
    }
}
