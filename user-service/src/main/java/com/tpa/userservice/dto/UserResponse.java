package com.tpa.userservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Builder
@Getter
public class UserResponse {
    String username;
    String email;
    Double eloRating;

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if(!(that instanceof UserResponse thatResponse)) return false;
        return this.username.equals(thatResponse.username) && Objects.equals(this.email, thatResponse.email);
    }
}

