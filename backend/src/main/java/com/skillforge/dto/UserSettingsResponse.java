package com.skillforge.dto;

import com.skillforge.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSettingsResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private Boolean emailNotificationsEnabled;
    private Boolean inAppNotificationsEnabled;
    private String themePreference;

    public static UserSettingsResponse from(User user) {
        UserSettingsResponse response = new UserSettingsResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setEmailNotificationsEnabled(user.getEmailNotificationsEnabled() == null || user.getEmailNotificationsEnabled());
        response.setInAppNotificationsEnabled(user.getInAppNotificationsEnabled() == null || user.getInAppNotificationsEnabled());
        response.setThemePreference(user.getThemePreference() == null ? "SYSTEM" : user.getThemePreference());
        return response;
    }
}
