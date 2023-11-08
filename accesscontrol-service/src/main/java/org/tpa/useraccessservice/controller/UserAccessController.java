package org.tpa.useraccessservice.controller;

import lombok.RequiredArgsConstructor;

import org.tpa.useraccessservice.dto.LoginRequest;
import org.tpa.useraccessservice.dto.RefreshTokenRequest;
import org.tpa.useraccessservice.dto.SignUpRequest;
import org.tpa.useraccessservice.service.UserAccessService;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAccessController {
    private final UserAccessService userAccessService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AccessTokenResponse> login(@RequestBody LoginRequest loginRequest){
        return userAccessService.login(loginRequest);
    }
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody SignUpRequest signUpRequest){
        userAccessService.registerUser(signUpRequest);
    }
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AccessTokenResponse> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        return userAccessService.refreshAccessToken(refreshTokenRequest);
    }

}
