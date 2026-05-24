package com.digitalemployee.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = -3716733579179068386L;

    private String type;
    private String description;

}
