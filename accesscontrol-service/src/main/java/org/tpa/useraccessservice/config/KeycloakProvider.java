package org.tpa.useraccessservice.config;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.Keycloak;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakProvider {
    @Value("${keycloak.realm}")
    public String realm;
    @Value("${keycloak.auth-server-url}")
    public String serverUrl;
    @Value("${keycloak.resource}")
    public String clientID;
    @Value("${keycloak.credentials.secret}")
    public String clientSecret;

    public Keycloak getInstance() {
            return KeycloakBuilder.builder()
                    .realm(realm)
                    .serverUrl(serverUrl)
                    .clientId(clientID)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .build();
    }

    public Keycloak newKeycloakBuilderWithPasswordCredentials(String username, String password) {
        return KeycloakBuilder.builder() //
                .realm(realm) //
                .serverUrl(serverUrl)//
                .clientId(clientID) //
                .clientSecret(clientSecret) //
                .grantType(OAuth2Constants.PASSWORD)
                .username(username) //
                .password(password)
                .build();
    }
}
