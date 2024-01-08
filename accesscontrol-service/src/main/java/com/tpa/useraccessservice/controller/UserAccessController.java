package com.tpa.useraccessservice.controller;

import com.tpa.useraccessservice.service.LogService;
import com.tpa.useraccessservice.service.UserAccessService;
import com.tpa.useraccessservice.type.LogType;
import lombok.RequiredArgsConstructor;

import com.tpa.useraccessservice.dto.LoginRequest;
import com.tpa.useraccessservice.dto.RefreshTokenRequest;
import com.tpa.useraccessservice.dto.SignUpRequest;

import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAccessController {
    private final UserAccessService userAccessService;
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AccessTokenResponse> login(@RequestBody LoginRequest loginRequest){
        AccessTokenResponse accessTokenResponse = userAccessService.login(loginRequest);
        return ResponseEntity.ok(accessTokenResponse);
    }
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<String> register(@RequestBody SignUpRequest signUpRequest){
        return userAccessService.registerUser(signUpRequest);
    }
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AccessTokenResponse> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        AccessTokenResponse accessTokenResponse = userAccessService.refreshAccessToken(refreshTokenRequest);
        return ResponseEntity.ok(accessTokenResponse);
    }

}
