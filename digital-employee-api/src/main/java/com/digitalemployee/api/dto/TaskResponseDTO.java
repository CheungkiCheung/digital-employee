package com.digitalemployee.api.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TaskResponseDTO implements Serializable {

    private static final long serialVersionUID = -5187644492140963628L;

    private String id;
    private String type;
    private String status;
    private String description;
    private Long startTime;
    private Long endTime;
    private String outputFile;

}
