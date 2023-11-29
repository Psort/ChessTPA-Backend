package com.tpa.queueservice.service;

import com.tpa.queueservice.controller.QueueController;
import com.tpa.queueservice.dto.NewGameRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
@Service
@RequiredArgsConstructor
public class PlayerConsumer {
    private final WebClient.Builder webClientBuilder;
    private static final int PLAYERS_REQUIRED = 2;

    private Queue<String> playerQueue = new LinkedList<>();

    private final QueueService queueService;
    @KafkaListener(topics = "players-queue", groupId = "queue-id")
    public void consume(String username) {
        playerQueue.add(username);
        if (playerQueue.size() >= PLAYERS_REQUIRED) {
            String firstPlayer = playerQueue.poll();
            String secondPlayer = playerQueue.poll();
            String newGameId =  webClientBuilder.build()
                    .post()
                    .uri("http://game-service/api/game/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(NewGameRequest.builder()
                            .firstPlayerUsername(firstPlayer)
                            .secondPlayerUsername(secondPlayer)
                            .build())
                    .retrieve().bodyToMono(String.class).block();
            queueService.startGame(newGameId);
        }
    }
}
