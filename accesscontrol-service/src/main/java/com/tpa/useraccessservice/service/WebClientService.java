package com.tpa.useraccessservice.service;

import com.tpa.useraccessservice.dto.SignUpRequest;
import com.tpa.useraccessservice.dto.UserResponse;
import com.tpa.useraccessservice.exception.AccessServerException;
import com.tpa.useraccessservice.type.LogType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
@Slf4j
public class WebClientService {
    private final LogService logService;
    private final WebClient.Builder webClientBuilderWithLB;
    private final WebClient.Builder webClientBuilder;

    public Mono<UserResponse> sendToUserService(SignUpRequest request) {
        return webClientBuilderWithLB.build()
                .post()
                .uri("http://user-service/api/user")
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.SERVICE_UNAVAILABLE,
                        clientResponse -> {
                            logService.send(LogType.ERROR, "User service is currently unavailable");
                            return Mono.error(new AccessServerException("User service is currently unavailable"));
                        }
                )
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> {
                            logService.send(LogType.ERROR, "User service returned an unexpected error");
                            return Mono.error(new AccessServerException("User service returned an unexpected error"));
                        }
                )
                .bodyToMono(UserResponse.class);
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
