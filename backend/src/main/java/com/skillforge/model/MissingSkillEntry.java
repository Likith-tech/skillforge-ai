package com.skillforge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** One row of an AtsAnalysis's missing-skills list: what's missing and how much it matters. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MissingSkillEntry {

    @Column(name = "skill_name", length = 80)
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 10)
    private SkillPriority priority;
}
