package com.skillforge.dto;

import com.skillforge.model.JobSkill;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobSkillResponse {

    private Long id;
    private String skillName;
    private String skillType;
    private Integer priorityRank;

    public static JobSkillResponse from(JobSkill skill) {
        JobSkillResponse response = new JobSkillResponse();
        response.setId(skill.getId());
        response.setSkillName(skill.getSkillName());
        response.setSkillType(skill.getSkillType().name());
        response.setPriorityRank(skill.getPriorityRank());
        return response;
    }
}