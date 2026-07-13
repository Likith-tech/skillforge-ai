package com.skillforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtsSubScores {
    private int formatting;
    private int skills;
    private int education;
    private int experience;
    private int projects;
    private int certifications;
}
