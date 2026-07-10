package com.skillforge.dto;

import com.skillforge.model.Notification;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationResponse {

    private Long id;
    private Long recipientId;
    private Long actorId;
    private String notificationType;
    private String channel;
    private String status;
    private String title;
    private String message;
    private String referenceType;
    private Long referenceId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public static NotificationResponse from(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setRecipientId(notification.getRecipientId());
        response.setActorId(notification.getActorId());
        response.setNotificationType(notification.getNotificationType().name());
        response.setChannel(notification.getChannel().name());
        response.setStatus(notification.getStatus().name());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setReferenceType(notification.getReferenceType());
        response.setReferenceId(notification.getReferenceId());
        response.setCreatedAt(notification.getCreatedAt());
        response.setReadAt(notification.getReadAt());
        return response;
    }
}