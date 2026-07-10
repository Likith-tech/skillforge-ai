package com.skillforge.dto;

import com.skillforge.model.LearningRoadmap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LearningRoadmapResponse {

    private Long id;
    private Long jobId;
    private Long recommendationId;
    private String status;
    private Integer totalSteps;
    private Integer completedSteps;
    private Integer inProgressSteps;
    private Integer pendingSteps;
    private List<LearningRoadmapStepResponse> steps;

    public static LearningRoadmapResponse from(LearningRoadmap roadmap, List<LearningRoadmapStepResponse> steps) {
        LearningRoadmapResponse response = new LearningRoadmapResponse();
        response.setId(roadmap.getId());
        response.setJobId(roadmap.getJob().getId());
        response.setRecommendationId(roadmap.getRecommendation().getId());
        response.setStatus(roadmap.getStatus().name());
        response.setTotalSteps(roadmap.getTotalSteps());
        response.setCompletedSteps(roadmap.getCompletedSteps());
        response.setInProgressSteps(roadmap.getInProgressSteps());
        response.setPendingSteps(roadmap.getPendingSteps());
        response.setSteps(steps);
        return response;
    }
}