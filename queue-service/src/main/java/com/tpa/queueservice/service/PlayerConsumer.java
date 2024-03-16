package com.tpa.queueservice.service;

import com.tpa.queueservice.dto.NewGameRequest;
import com.tpa.queueservice.event.QueueEvent;
import com.tpa.queueservice.type.GameType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlayerConsumer {
    private final WebClient.Builder webClientBuilder;
    private static final int PLAYERS_REQUIRED = 2;
    private Map<Double, List<String>> oneMinQueue = new HashMap<>();
    private Map<Double, List<String>> threeMinQueue = new HashMap<>();
    private Map<Double, List<String>> fiveMinQueue = new HashMap<>();
    private Map<Double, List<String>> tenMinQueue = new HashMap<>();
    private Map<Double, List<String>> unlimitedQueue = new HashMap<>();


    private final QueueService queueService;

    @KafkaListener(topics = "one-min-queue", groupId = "queue-id")
    public void setOneMinQueue(QueueEvent queueEvent) {
        addToQueue(queueEvent, oneMinQueue);
    }
    @KafkaListener(topics = "three-min-queue", groupId = "queue-id")
    public void consume(QueueEvent queueEvent) {
        addToQueue(queueEvent, threeMinQueue);
    }

    @KafkaListener(topics = "five-min-queue", groupId = "queue-id")
    public void setFiveMinQueue(QueueEvent queueEvent) {
        addToQueue(queueEvent, fiveMinQueue);
    }
    @KafkaListener(topics = "ten-min-queue", groupId = "queue-id")
    public void setTenMinQueue(QueueEvent queueEvent) {
        addToQueue(queueEvent, tenMinQueue);
    }

    @KafkaListener(topics = "unLimited-queue", groupId = "queue-id")
    public void setUnlimitedQueue(QueueEvent queueEvent) {
        addToQueue(queueEvent, unlimitedQueue);
    }

    private void addToQueue(QueueEvent queueEvent, Map<Double, List<String>> Queue) {
        Double calculatedQueue = calculateQueue(queueEvent.getEloRating());
        addUserToQueue(Queue, calculatedQueue, queueEvent.getUsername());
        List<String> eloRatingQueue = Queue.get(calculatedQueue);

        if (eloRatingQueue.size() >= PLAYERS_REQUIRED) {
            String firstPlayer = removeLastPlayer(eloRatingQueue);
            String secondPlayer = removeLastPlayer(eloRatingQueue);
            creteGame(firstPlayer,secondPlayer, queueEvent.getGameType());
        }
    }

    private static void addUserToQueue(Map<Double, List<String>> map, Double key, String value) {
        if (map.containsKey(key)) {
            List<String> valuesList = map.get(key);
            valuesList.add(value);
        } else {
            List<String> newList = new ArrayList<>(List.of(value));
            map.put(key, newList);
        }
    }
    private void creteGame(String firstPlayer, String secondPlayer, GameType gameType){
        String newGameId =  webClientBuilder.build()
                .post()
                .uri("http://game-service/api/game/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(NewGameRequest.builder()
                        .firstPlayerUsername(firstPlayer)
                        .secondPlayerUsername(secondPlayer)
                        .gameType(gameType)
                        .build())
                .retrieve().bodyToMono(String.class).block();
        queueService.startGame(newGameId);
    }

    private String removeLastPlayer(List<String> queue) {
        if (!queue.isEmpty()) {
            return queue.remove(0);
        } else {
            return null;
        }
    }
    //TO DO Better calculateElo
    private Double calculateQueue(Double eloRating) {
        Double queue = 0.0;
        int lowerLimit = 0;
        int upperLimit = 100;

        while (!(lowerLimit <=eloRating) || !(eloRating <= upperLimit)){
            lowerLimit+=100;
            upperLimit+=100;
            queue +=1;
        }

        return queue;
    }
}

