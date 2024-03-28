package com.tpa.websocketservice.dto;

import lombok.Data;

@Data
public class MessageRequest {
    String gameId;
    String username;
    String message;
}
