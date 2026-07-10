package com.skillforge.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobGapRecommendationResponse {
    private JobRecommendationResponse recommendation;
    private SkillGapResponse skillGap;
    private LearningRoadmapResponse roadmap;
}