package com.tpa.useraccessservice.service;

import com.tpa.useraccessservice.dto.SignUpRequest;
import com.tpa.useraccessservice.dto.UserResponse;
import com.tpa.useraccessservice.exception.AccessRequestException;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class WebClientService {
    private final WebClient.Builder webClientBuilderWithLB;
    private final WebClient.Builder webClientBuilder;

    public void sendToUserService(SignUpRequest request) {
        webClientBuilderWithLB.build()
                .post()
                .uri("http://user-service/api/user")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorMap(RuntimeException.class, ex -> new AccessRequestException("User already exist"))
                .block();
    }

    public AccessTokenResponse getRefreshTokenFormKeycloak(HttpHeaders headers, MultiValueMap<String, String> requestBody) {
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8181/realms/Chess-TPA/protocol/openid-connect/token")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .block();
    }
}
