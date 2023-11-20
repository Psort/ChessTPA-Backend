package com.tpa.websocketservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final WebClient.Builder webClientBuilder;
    public String getGame(String gameId) {

        return webClientBuilder.baseUrl("http://game-service")
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/game/json")
                        .queryParam("gameId", gameId)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();


    }
}
