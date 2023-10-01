package org.example.controller;

import lombok.RequiredArgsConstructor;

import org.example.dto.LoginRequest;
import org.example.service.UserAccessService;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAccessController {
    private final UserAccessService userAccessService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AccessTokenResponse> Login(@RequestBody LoginRequest loginRequest){
        return userAccessService.getAccessToken(loginRequest);
    }


}
