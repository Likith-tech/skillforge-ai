package com.skillforge.dto;

import com.skillforge.model.RoadmapStep;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LearningRoadmapStepResponse {

    private Long id;
    private String skillName;
    private String level;
    private String resourceTitle;
    private String resourceUrl;
    private Integer estimatedDurationHours;
    private String status;
    private Integer stepOrder;
    private LocalDateTime createdAt;

    public static LearningRoadmapStepResponse from(RoadmapStep step) {
        LearningRoadmapStepResponse response = new LearningRoadmapStepResponse();
        response.setId(step.getId());
        response.setSkillName(step.getSkillName());
        response.setLevel(step.getLevel().name());
        response.setResourceTitle(step.getResourceTitle());
        response.setResourceUrl(step.getResourceUrl());
        response.setEstimatedDurationHours(step.getEstimatedDurationHours());
        response.setStatus(step.getStatus().name());
        response.setStepOrder(step.getStepOrder());
        response.setCreatedAt(step.getCreatedAt());
        return response;
    }
}