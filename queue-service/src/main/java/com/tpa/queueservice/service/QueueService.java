package com.tpa.queueservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class QueueService {
    private static final String TOPIC = "players-queue";
    private CompletableFuture<String> gameStartFuture = new CompletableFuture<>();

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ResponseEntity<String> addToQueue(String playerId) {
        try {
            kafkaTemplate.send(TOPIC, playerId);
            String result = gameStartFuture.get();
            gameStartFuture = new CompletableFuture<>();
            return ResponseEntity.ok(result);
        } catch (InterruptedException | ExecutionException e) {
            // toDo
        }
        return (ResponseEntity<String>) ResponseEntity.internalServerError();
    }
    public void startGame(String gameId) {
        gameStartFuture.complete(gameId);
    }

}
