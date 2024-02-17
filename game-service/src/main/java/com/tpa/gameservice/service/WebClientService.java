package com.tpa.gameservice.service;

import com.tpa.gameservice.dto.GameToUserRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@AllArgsConstructor
public class WebClientService {

    private final WebClient.Builder webClientBuilder;


    public void sendGameToUserService(String gameId, String firstPlayerUsername, String secondPlayerUsername) {

        GameToUserRequest request = GameToUserRequest.builder()
                .gameId(gameId)
                .firstPlayerUsername(firstPlayerUsername)
                .secondPlayerUsername(secondPlayerUsername)
                .build();

        webClientBuilder.build().post().uri("http://user-service/api/user/game")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
