package org.tpa.useraccessservice.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    String email;
    String password;
}
