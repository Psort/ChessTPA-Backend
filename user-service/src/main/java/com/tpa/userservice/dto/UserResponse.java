package com.tpa.userservice.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserResponse {
    String username;
    String email;
}
