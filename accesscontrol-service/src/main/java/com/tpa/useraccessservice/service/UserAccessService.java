package com.tpa.useraccessservice.service;

import com.tpa.useraccessservice.type.LogType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.tpa.useraccessservice.config.KeycloakProvider;
import com.tpa.useraccessservice.dto.LoginRequest;
import com.tpa.useraccessservice.dto.RefreshTokenRequest;
import com.tpa.useraccessservice.dto.SignUpRequest;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.keycloak.admin.client.Keycloak;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@Service
public class UserAccessService {
    
    @Value("${keycloak.realm}")
    public String realm;
    @Value("${keycloak.resource}")
    public String clientID;
    @Value("${keycloak.credentials.secret}")
    public String clientSecret;
    
    private final KeycloakProvider keycloakProvider;
    private final LogService logService;
    private final WebClientService webClientService;

    /**
     * login user through keycloack
     * @param loginRequest
     * @return AccessTokenResponse
     */
    public AccessTokenResponse login(LoginRequest loginRequest){
        Keycloak keycloak = keycloakProvider.newKeycloakBuilderWithPasswordCredentials(loginRequest.getEmail(), loginRequest.getPassword());
        return  keycloak.tokenManager().getAccessToken();
    }

    /**
     * creates new user in keycloack
     * @param request
     */
    @CircuitBreaker(name = "user-service", fallbackMethod = "fallback")
    @TimeLimiter(name = "user-service")
    @Retry(name = "user-service")
    public CompletableFuture<String> registerUser(SignUpRequest request){
        UsersResource usersResource = keycloakProvider.getInstance().realm(realm).users();
        UserRepresentation kcUser = createKeycloakUser(request);
        webClientService.sendUserToAddInUserService(request);
        usersResource.create(kcUser);
        logService.send(LogType.INFO,"User registered successfully");
        return CompletableFuture.completedFuture("User registered successfully");
    }

    /**
     * get new access token from keycloak using refresh token
     * @param refreshTokenRequest
     * @return AccessTokenResponse
     */

    public AccessTokenResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {
        //RequestBody
        MultiValueMap<String, String> requestBody = createRequestBodyParam(refreshTokenRequest);
        //Headers
        HttpHeaders headers = createHeaders();

        return webClientService.getRefreshTokenFormKeycloak(headers,requestBody);
    }


    /**
     * creates password credentials for new keycloack user
     * @param password
     * @return CredentialRepresentation
     */
    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    private CompletableFuture<String> fallback(SignUpRequest signUpRequest, RuntimeException e) {
        logService.send(LogType.ERROR,"notok");
        return CompletableFuture.supplyAsync(() -> "Oops something went wrong, try to register later");
    }

    private CompletableFuture<String> fallback(SignUpRequest signUpRequest, TimeoutException e) {
        logService.send(LogType.ERROR,"notok");
        return CompletableFuture.supplyAsync(() -> "Oops something went wrong, try to register later");
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private MultiValueMap<String, String> createRequestBodyParam(RefreshTokenRequest refreshTokenRequest) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientID);
        requestBody.add("grant_type", OAuth2Constants.REFRESH_TOKEN);
        requestBody.add("refresh_token", refreshTokenRequest.getRefreshToken());
        requestBody.add("client_secret", clientSecret);
        return requestBody;
    }

    private UserRepresentation createKeycloakUser(SignUpRequest request) {
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(request.getPassword());
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(request.getUsername());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setEmail(request.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);
        return kcUser;
    }
}
