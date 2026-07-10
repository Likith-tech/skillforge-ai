package com.skillforge.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortalStatsResponse {

    private List<KeyValuePoint> cards;
    private List<KeyValuePoint> charts;
    private List<String> highlights;

    @Getter
    @Setter
    public static class KeyValuePoint {
        private String label;
        private Long value;
    }
}