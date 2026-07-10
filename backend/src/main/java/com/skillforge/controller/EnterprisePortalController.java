package com.skillforge.controller;

import com.skillforge.dto.ApiResponse;
import com.skillforge.dto.AuditLogResponse;
import com.skillforge.dto.FileAssetResponse;
import com.skillforge.dto.NotificationResponse;
import com.skillforge.dto.PortalStatsResponse;
import com.skillforge.dto.SearchResponse;
import com.skillforge.model.FileAssetType;
import com.skillforge.model.NotificationStatus;
import com.skillforge.security.UserPrincipal;
import com.skillforge.service.AdminPortalService;
import com.skillforge.service.AuditLogService;
import com.skillforge.service.FileAssetService;
import com.skillforge.service.NotificationService;
import com.skillforge.service.SearchService;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/enterprise")
public class EnterprisePortalController {

    private final AdminPortalService adminPortalService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final SearchService searchService;
    private final FileAssetService fileAssetService;

    public EnterprisePortalController(AdminPortalService adminPortalService, NotificationService notificationService, AuditLogService auditLogService, SearchService searchService, FileAssetService fileAssetService) {
        this.adminPortalService = adminPortalService;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
        this.searchService = searchService;
        this.fileAssetService = fileAssetService;
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PortalStatsResponse> adminDashboard() {
        return ApiResponse.success("Admin dashboard loaded", adminPortalService.dashboard());
    }

    @GetMapping("/admin/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<AuditLogResponse>> auditLogs() {
        return ApiResponse.success("Audit logs loaded", auditLogService.latest());
    }

    @GetMapping("/notifications")
    @PreAuthorize("hasAnyRole('STUDENT', 'RECRUITER', 'ADMIN')")
    public ApiResponse<List<NotificationResponse>> notifications(@AuthenticationPrincipal UserPrincipal principal, @RequestParam(required = false) NotificationStatus status) {
        return ApiResponse.success("Notifications loaded", notificationService.list(principal.getId(), status));
    }

    @GetMapping("/notifications/unread-count")
    @PreAuthorize("hasAnyRole('STUDENT', 'RECRUITER', 'ADMIN')")
    public ApiResponse<Long> unreadCount(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Unread count loaded", notificationService.unreadCount(principal.getId()));
    }

    @PutMapping("/notifications/{notificationId}/read")
    @PreAuthorize("hasAnyRole('STUDENT', 'RECRUITER', 'ADMIN')")
    public ApiResponse<NotificationResponse> markRead(@AuthenticationPrincipal UserPrincipal principal, @org.springframework.web.bind.annotation.PathVariable @Positive Long notificationId) {
        return ApiResponse.success("Notification updated", notificationService.markRead(principal.getId(), notificationId));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('STUDENT', 'RECRUITER', 'ADMIN')")
    public ApiResponse<SearchResponse> search(@RequestParam String q) {
        return ApiResponse.success("Search completed", searchService.search(q));
    }

    @GetMapping("/files")
    @PreAuthorize("hasAnyRole('STUDENT', 'RECRUITER', 'ADMIN')")
    public ApiResponse<List<FileAssetResponse>> files(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Files loaded", fileAssetService.list(principal.getId()));
    }

    @PostMapping(value = "/files/photo", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('STUDENT', 'RECRUITER', 'ADMIN')")
    public ApiResponse<FileAssetResponse> uploadPhoto(@AuthenticationPrincipal UserPrincipal principal, @RequestPart MultipartFile file) {
        return ApiResponse.success("Photo stored", fileAssetService.save(principal.getId(), FileAssetType.PROFILE_PHOTO, file));
    }

    @PostMapping(value = "/files/company-logo", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('RECRUITER')")
    public ApiResponse<FileAssetResponse> uploadLogo(@AuthenticationPrincipal UserPrincipal principal, @RequestPart MultipartFile file) {
        return ApiResponse.success("Logo stored", fileAssetService.save(principal.getId(), FileAssetType.COMPANY_LOGO, file));
    }

    @DeleteMapping("/files/{fileId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'RECRUITER', 'ADMIN')")
    public ApiResponse<Void> deleteFile(@AuthenticationPrincipal UserPrincipal principal, @org.springframework.web.bind.annotation.PathVariable @Positive Long fileId) {
        fileAssetService.delete(principal.getId(), fileId);
        return ApiResponse.success("File removed", null);
    }

    @DeleteMapping("/notifications/{notificationId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'RECRUITER', 'ADMIN')")
    public ApiResponse<Void> deleteNotification(@AuthenticationPrincipal UserPrincipal principal, @org.springframework.web.bind.annotation.PathVariable @Positive Long notificationId) {
        notificationService.delete(principal.getId(), notificationId);
        return ApiResponse.success("Notification deleted", null);
    }

    @GetMapping("/me/role")
    @PreAuthorize("hasAnyRole('STUDENT', 'RECRUITER', 'ADMIN')")
    public ApiResponse<String> myRole(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Role loaded", principal.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
    }
}