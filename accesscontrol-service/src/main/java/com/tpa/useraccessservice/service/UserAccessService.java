package com.tpa.useraccessservice.service;

import com.tpa.useraccessservice.config.KeycloakProvider;
import com.tpa.useraccessservice.dto.LoginRequest;
import com.tpa.useraccessservice.dto.RefreshTokenRequest;
import com.tpa.useraccessservice.dto.SignUpRequest;
import com.tpa.useraccessservice.dto.UserResponse;
import com.tpa.useraccessservice.exception.AccessRequestException;
import com.tpa.useraccessservice.exception.AccessServerException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.ProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Collections;

@RequiredArgsConstructor
@Service
@Slf4j
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
        try {
            Keycloak keycloak = keycloakProvider.newKeycloakBuilderWithPasswordCredentials(loginRequest.getEmail(), loginRequest.getPassword());
            return keycloak.tokenManager().getAccessToken();

        } catch (ProcessingException e) {
            logService.sendError( "Error during user login, keycloak server is shut down");
            throw new AccessServerException("Server problem, please try again later");

        }  catch (NotAuthorizedException e) {
            logService.sendError( "Invalid username or password, please try again");
            throw new AccessRequestException("Invalid username or password, please try again");
        }
    }

    /**
     * creates new user in keycloack
     * @param request
     */
    @Transactional
    public Mono<UserResponse> registerUser(SignUpRequest request) {
        if(isKeycloakServerAvailable()) {
            return webClientService.sendToUserService(request)
                    .flatMap(response -> {
                        connectWithKeycloak(request);
                        logService.sendError( "User {} registered successfully", response.getUsername());
                        return Mono.just(response);
                    })
                    .onErrorMap(throwable -> {
                        log.error("Error during registration", throwable);
                        logService.sendError("Error during registration");
                        return new AccessServerException(throwable.getMessage());
                    });
        } else throw new AccessServerException("KEYCLOAK IS DEAD");

    }
    public boolean isKeycloakServerAvailable() {
        String keycloakServerHost = "localhost";
        int keycloakServerPort = 8181;

        try (Socket ignored = new Socket(keycloakServerHost, keycloakServerPort)) {
            return true;

        } catch (IOException e) {
            logService.sendError("Keycloak server is not available");
            return false;
        }
    }
    private void connectWithKeycloak(SignUpRequest request) {
        UsersResource usersResource = keycloakProvider.getInstance().realm(realm).users();
        UserRepresentation kcUser = createKeycloakUser(request);
        usersResource.create(kcUser);
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
