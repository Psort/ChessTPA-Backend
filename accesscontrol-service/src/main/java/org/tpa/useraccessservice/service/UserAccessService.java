package org.tpa.useraccessservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.tpa.useraccessservice.config.KeycloakProvider;
import org.tpa.useraccessservice.dto.LoginRequest;
import org.tpa.useraccessservice.dto.RefreshTokenRequest;
import org.tpa.useraccessservice.dto.SignUpRequest;
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
    private final KeycloakProvider keycloakProvider;
    private final WebClient.Builder webClientBuilderWithLB;
    private final WebClient.Builder webClientBuilder;
    @Value("${keycloak.realm}")
    public String realm;
    @Value("${keycloak.resource}")
    public String clientID;
    @Value("${keycloak.credentials.secret}")
    public String clientSecret;

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
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(request.getPassword());
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(request.getUsername());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setEmail(request.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);

        webClientBuilderWithLB.build().post().uri("http://user-service/api/user")
                .bodyValue(request)
                .retrieve()
                        .toBodilessEntity()
                                .block();

        usersResource.create(kcUser);

        return CompletableFuture.completedFuture("User registered successfully");
    }
    /**
     * get new access token from keycloak using refresh token
     * @param refreshTokenRequest
     * @return AccessTokenResponse
     */

    public AccessTokenResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientID);
        requestBody.add("grant_type", OAuth2Constants.REFRESH_TOKEN);
        requestBody.add("refresh_token", refreshTokenRequest.getRefreshToken());
        requestBody.add("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        AccessTokenResponse accessTokenResponse = webClientBuilder.build()
                .post()
                .uri("http://localhost:8181/realms/Chess-TPA/protocol/openid-connect/token")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .block();
        return accessTokenResponse;
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
        return CompletableFuture.supplyAsync(() -> "Oops something went wrong, try to register later");
    }
    private CompletableFuture<String> fallback(SignUpRequest signUpRequest, TimeoutException e) {
        return CompletableFuture.supplyAsync(() -> "Oops something went wrong, try to register later");
    }
}
