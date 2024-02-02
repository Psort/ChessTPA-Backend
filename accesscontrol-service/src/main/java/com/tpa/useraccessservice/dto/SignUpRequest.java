package com.tpa.useraccessservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
     String username;
     String email;
     String password;
}
