package com.skillforge.controller;

import com.skillforge.dto.ApiResponse;
import com.skillforge.dto.ChangePasswordRequest;
import com.skillforge.dto.UserSettingsRequest;
import com.skillforge.dto.UserSettingsResponse;
import com.skillforge.security.UserPrincipal;
import com.skillforge.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasAnyRole('STUDENT', 'RECRUITER', 'ADMIN')")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/settings")
    public ApiResponse<UserSettingsResponse> getSettings(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("User settings loaded", userAccountService.getSettings(principal.getId()));
    }

    @PutMapping("/settings")
    public ApiResponse<UserSettingsResponse> updateSettings(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserSettingsRequest request
    ) {
        return ApiResponse.success("User settings updated", userAccountService.updateSettings(principal.getId(), request));
    }

    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userAccountService.changePassword(principal.getId(), request);
        return ApiResponse.success("Password changed successfully", null);
    }
}
