package com.digitalemployee.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ConversationMessageRequestDTO implements Serializable {

    private static final long serialVersionUID = 6194072608177656891L;

    private String message;

}
