package com.skillforge.service;

import com.skillforge.dto.AuditLogResponse;
import com.skillforge.model.AuditAction;
import com.skillforge.model.AuditLog;
import com.skillforge.repository.AuditLogRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(Long actorId, AuditAction action, String entityType, Long entityId, String details, String ipAddress) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActorId(actorId);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDetails(details);
        auditLog.setIpAddress(ipAddress);
        auditLogRepository.save(auditLog);
    }

    public List<AuditLogResponse> latest() {
        return auditLogRepository.findTop100ByOrderByCreatedAtDesc().stream().map(AuditLogResponse::from).toList();
    }
}