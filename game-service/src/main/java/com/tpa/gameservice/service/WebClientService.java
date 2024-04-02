package com.tpa.gameservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.gameservice.dto.ComputerMoveRequest;
import com.tpa.gameservice.dto.ComputerMoveResponse;
import com.tpa.gameservice.dto.GameToUserRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class WebClientService {

    private final WebClient.Builder webClientBuilderWithLB;
    private final WebClient.Builder webClientBuilder;


    public void sendGameToUserService(String gameId, String firstPlayerUsername, String secondPlayerUsername) {

        GameToUserRequest request = GameToUserRequest.builder()
                .gameId(gameId)
                .firstPlayerUsername(firstPlayerUsername)
                .secondPlayerUsername(secondPlayerUsername)
                .build();

        webClientBuilderWithLB.build().post().uri("http://user-service/api/user/game")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
    public Double getUserEloRating(String username) {
        return webClientBuilderWithLB.build().get().uri("http://user-service/api/user/elo?username=" + username)
                .retrieve()
                .bodyToMono(Double.class)
                .block();
    }

    public String getComputerMove(ComputerMoveRequest computerMoveRequest) {
        String uri = String.format("http://localhost:5000/api/analysis/computer?eloRating=%s&fenBody=%s",
                computerMoveRequest.getEloRating(),
                computerMoveRequest.getFenBody());
        return webClientBuilder.build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }
}
