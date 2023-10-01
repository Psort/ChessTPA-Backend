package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.KeycloakProvider;
import org.example.dto.LoginRequest;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.keycloak.admin.client.Keycloak;

@RequiredArgsConstructor
@Service
public class UserAccessService {
    private final KeycloakProvider keycloakProvider;

    public ResponseEntity<AccessTokenResponse> getAccessToken(LoginRequest loginRequest){
        Keycloak keycloak = keycloakProvider.newKeycloakBuilderWithPasswordCredentials(loginRequest.getEmail(), loginRequest.getPassword()).build();
        return  ResponseEntity.status(HttpStatus.OK).body(keycloak.tokenManager().getAccessToken());
    }
    public void registerUser(){

    }
}
