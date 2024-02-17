package com.tpa.queueservice.service;

import com.tpa.queueservice.dto.NewGameRequest;
import com.tpa.queueservice.event.QueueEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedList;
import java.util.Queue;
@Service
@RequiredArgsConstructor
public class PlayerConsumer {
    private final WebClient.Builder webClientBuilder;
    private static final int PLAYERS_REQUIRED = 2;

    private Queue<String> oneMinQueue = new LinkedList<>();
    private Queue<String> threeMinQueue = new LinkedList<>();
    private Queue<String> fiveMinQueue = new LinkedList<>();
    private Queue<String> tenMinQueue = new LinkedList<>();
    private Queue<String> unlimitedQueue = new LinkedList<>();

    private final QueueService queueService;

    @KafkaListener(topics = "one-min-queue", groupId = "queue-id")
    public void setOneMinQueue(QueueEvent queueEvent) {
        oneMinQueue.add(queueEvent.getUsername());
        if (oneMinQueue.size() >= PLAYERS_REQUIRED) {
            String firstPlayer = oneMinQueue.poll();
            String secondPlayer = oneMinQueue.poll();
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
    @KafkaListener(topics = "three-min-queue", groupId = "queue-id")
    public void consume(QueueEvent queueEvent) {
        threeMinQueue.add(queueEvent.getUsername());
        if (threeMinQueue.size() >= PLAYERS_REQUIRED) {
            String firstPlayer = threeMinQueue.poll();
            String secondPlayer = threeMinQueue.poll();
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

    @KafkaListener(topics = "five-min-queue", groupId = "queue-id")
    public void setFiveMinQueue(QueueEvent queueEvent) {
        fiveMinQueue.add(queueEvent.getUsername());
        System.out.println(queueEvent.getUsername());
        System.out.println(queueEvent.getEloRating());
        if (fiveMinQueue.size() >= PLAYERS_REQUIRED) {
            String firstPlayer = fiveMinQueue.poll();
            String secondPlayer = fiveMinQueue.poll();
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
    @KafkaListener(topics = "ten-min-queue", groupId = "queue-id")
    public void setTenMinQueue(QueueEvent queueEvent) {
        tenMinQueue.add(queueEvent.getUsername());
        if (tenMinQueue.size() >= PLAYERS_REQUIRED) {
            String firstPlayer = tenMinQueue.poll();
            String secondPlayer = tenMinQueue.poll();
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
    @KafkaListener(topics = "unLimited-queue", groupId = "queue-id")
    public void setUnlimitedQueue(QueueEvent queueEvent) {
        unlimitedQueue.add(queueEvent.getUsername());
        if (unlimitedQueue.size() >= PLAYERS_REQUIRED) {
            String firstPlayer = unlimitedQueue.poll();
            String secondPlayer = unlimitedQueue.poll();
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
