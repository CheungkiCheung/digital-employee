package com.digitalemployee.api.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ToolExecutionDTO implements Serializable {

    private static final long serialVersionUID = 7232931745329732464L;

    private String toolName;
    private String input;
    private String permissionBehavior;
    private String result;

}
