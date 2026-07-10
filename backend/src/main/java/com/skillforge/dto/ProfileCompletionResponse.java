package com.skillforge.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileCompletionResponse {

    private int completionPercentage;
    private List<String> missingFields;
}
