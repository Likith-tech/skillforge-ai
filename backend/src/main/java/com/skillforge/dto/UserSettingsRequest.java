package com.skillforge.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSettingsRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private Boolean emailNotificationsEnabled;

    private Boolean inAppNotificationsEnabled;

    @Pattern(regexp = "LIGHT|DARK|SYSTEM", message = "Theme preference must be LIGHT, DARK, or SYSTEM")
    private String themePreference;
}
