package com.skillforge.service;

import com.skillforge.dto.NotificationResponse;
import com.skillforge.model.Notification;
import com.skillforge.model.NotificationChannel;
import com.skillforge.model.NotificationStatus;
import com.skillforge.model.NotificationType;
import com.skillforge.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final @Nullable JavaMailSender mailSender;

    public NotificationService(NotificationRepository notificationRepository, @Nullable JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
    }

    public List<NotificationResponse> list(Long recipientId, NotificationStatus status) {
        List<Notification> notifications = status == null
                ? notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId)
                : notificationRepository.findByRecipientIdAndStatusOrderByCreatedAtDesc(recipientId, status);
        return notifications.stream().map(NotificationResponse::from).toList();
    }

    public long unreadCount(Long recipientId) {
        return notificationRepository.countByRecipientIdAndStatus(recipientId, NotificationStatus.UNREAD);
    }

    @Transactional
    public void delete(Long recipientId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        if (!notification.getRecipientId().equals(recipientId)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Cannot modify this notification");
        }
        notificationRepository.delete(notification);
    }

    @Transactional
    public NotificationResponse markRead(Long recipientId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        if (!notification.getRecipientId().equals(recipientId)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Cannot modify this notification");
        }
        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        return NotificationResponse.from(notificationRepository.save(notification));
    }

    @Transactional
    public NotificationResponse create(Long recipientId, Long actorId, NotificationType type, NotificationChannel channel, String title, String message, String referenceType, Long referenceId, String email) {
        Notification notification = new Notification();
        notification.setRecipientId(recipientId);
        notification.setActorId(actorId);
        notification.setNotificationType(type);
        notification.setChannel(channel);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReferenceType(referenceType);
        notification.setReferenceId(referenceId);
        notification.setStatus(NotificationStatus.UNREAD);
        Notification saved = notificationRepository.save(notification);
        sendEmailIfPossible(email, title, message);
        return NotificationResponse.from(saved);
    }

    private void sendEmailIfPossible(String email, String subject, String body) {
        if (mailSender == null || email == null || email.isBlank()) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (MailException ignored) {
        }
    }
}