package com.tpa.queueservice.service;

import com.tpa.queueservice.dto.QueueRequest;
import com.tpa.queueservice.event.QueueEvent;
import com.tpa.queueservice.type.GameType;
import com.tpa.queueservice.type.LogType;
import com.tpa.queueservice.type.QueueType;
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
    private CompletableFuture<String> gameStartFuture = new CompletableFuture<>();
    private final LogService logService;

    private final KafkaTemplate<String, QueueEvent> kafkaTemplate;

    public String addToQueue(QueueRequest queueRequest){
        try {
            kafkaTemplate.send(
                    queueRequest.getQueueType().getQueueStringType(),
                    QueueEvent.builder()
                            .username(queueRequest.getUsername())
                            .eloRating(queueRequest.getEloRating())
                            .gameType(mapType(queueRequest.getQueueType()))
                            .build());
            String result = gameStartFuture.get();
            gameStartFuture = new CompletableFuture<>();
            return result;
        } catch (InterruptedException | ExecutionException e) {
            logService.sendError("todo");
            return "toDo";
        }
    }
    public void startGame(String gameId) {
        gameStartFuture.complete(gameId);
    }

    private GameType mapType(QueueType queueType) {

        switch (queueType) {
            case ONEMINQUEUE -> {
                return GameType.ONE;
            }
            case TENMINQUEUE -> {
                return GameType.TEN;
            }
            case THREEMINQUEUE -> {
                return GameType.THREE;
            }
            case FIVEMINQUEUE -> {
                return GameType.FIVE;
            }
            case UNLIMITEDQUEUE -> {
                return GameType.INFINITE;
            }
            default -> throw new RuntimeException("Invalid queue type");
        }
    }
}
