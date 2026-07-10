package com.skillforge.dto;

import com.skillforge.model.JobLocation;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobLocationResponse {

    private Long id;
    private String displayName;
    private String city;
    private String state;
    private String country;
    private Boolean remoteFriendly;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static JobLocationResponse from(JobLocation location) {
        JobLocationResponse response = new JobLocationResponse();
        response.setId(location.getId());
        response.setDisplayName(location.getDisplayName());
        response.setCity(location.getCity());
        response.setState(location.getState());
        response.setCountry(location.getCountry());
        response.setRemoteFriendly(location.getRemoteFriendly());
        response.setCreatedAt(location.getCreatedAt());
        response.setUpdatedAt(location.getUpdatedAt());
        return response;
    }
}