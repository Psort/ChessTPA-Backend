package com.tpa.userservice.dto;

import lombok.Getter;

@Getter
public class EloTradeRequest {
    private String winningUsername;
    private String losingUsername;
}
