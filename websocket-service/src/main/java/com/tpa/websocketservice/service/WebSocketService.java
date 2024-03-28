package com.tpa.websocketservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.websocketservice.dto.MessageRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
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

    public String getMessages(MessageRequest messageRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(messageRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
