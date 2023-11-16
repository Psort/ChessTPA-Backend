package com.tpa.gameservice.controller;


import com.tpa.gameservice.dto.GameResponse;
import com.tpa.gameservice.dto.NewGameRequest;
import com.tpa.gameservice.dto.SafeGameStateRequest;
import com.tpa.gameservice.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createGame(@RequestBody NewGameRequest newGameRequest){
        return gameService.createGame(newGameRequest);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GameResponse> getGame(@RequestParam String gameId){
        return gameService.getGame(gameId);
    }
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public void safeGameStatus(@RequestBody SafeGameStateRequest safeGameStatusRequest){
        gameService.safeGameState(safeGameStatusRequest);
    }
}
