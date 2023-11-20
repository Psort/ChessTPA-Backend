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

    public String addToQueue(String username) {
        try {
            kafkaTemplate.send(TOPIC, username);
            String result = gameStartFuture.get();
            gameStartFuture = new CompletableFuture<>();
            return result;
        } catch (InterruptedException | ExecutionException e) {
            // toDo
        }
        return username;
    }
    public void startGame(String gameId) {
        gameStartFuture.complete(gameId);
    }

}
