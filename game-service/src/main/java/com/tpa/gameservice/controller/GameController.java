package com.tpa.gameservice.controller;


import com.tpa.gameservice.dto.GameResponse;
import com.tpa.gameservice.dto.NewGameRequest;
import com.tpa.gameservice.dto.SafeGameStateRequest;
import com.tpa.gameservice.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createGame(@RequestBody NewGameRequest newGameRequest){
        String gameId = gameService.createGame(newGameRequest);
        return ResponseEntity.ok(gameId);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GameResponse> getGame(@RequestParam String gameId){
        GameResponse gameResponse = gameService.getGame(gameId);
        return ResponseEntity.ok(gameResponse);
    }
    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<GameResponse>> getAllGamesForUser(@RequestParam String username){
        List<GameResponse> gameResponse = gameService.getAllGamesForUser(username);
        return ResponseEntity.ok(gameResponse);
    }
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public void safeGameStatus(@RequestBody SafeGameStateRequest safeGameStatusRequest){
        gameService.safeGameState(safeGameStatusRequest);
    }
    @GetMapping("/json")
    @ResponseStatus(HttpStatus.OK)
    public String getGameAsJson(@RequestParam String gameId){
        return gameService.getGameResponseAsJson(gameId);
    }
    @GetMapping("/moves")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<String>> getMovesHistory(@RequestParam String gameId){
        return ResponseEntity.ok(gameService.getMovesHistory(gameId));
    }
}
