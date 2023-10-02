package org.tpa.useraccessservice.service;

import lombok.RequiredArgsConstructor;
import org.tpa.useraccessservice.config.KeycloakProvider;
import org.tpa.useraccessservice.dto.LoginRequest;
import org.tpa.useraccessservice.dto.SignUpRequest;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.keycloak.admin.client.Keycloak;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserAccessService {
    private final KeycloakProvider keycloakProvider;
    @Value("${keycloak.realm}")
    public String realm;

    /**
     * login user through keycloack
     * @param loginRequest
     * @return AccessTokenResponse
     */
    public ResponseEntity<AccessTokenResponse> login(LoginRequest loginRequest){
        Keycloak keycloak = keycloakProvider.newKeycloakBuilderWithPasswordCredentials(loginRequest.getEmail(), loginRequest.getPassword()).build();
        return  ResponseEntity.status(HttpStatus.OK).body(keycloak.tokenManager().getAccessToken());
    }

    /**
     * creates new user in keycloack
     * @param request
     */
    public void registerUser(SignUpRequest request){
        UsersResource usersResource = keycloakProvider.getInstance().realm(realm).users();
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(request.getPassword());

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(request.getEmail());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setEmail(request.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);

        usersResource.create(kcUser);
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
}
