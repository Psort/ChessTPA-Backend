package com.tpa.gameservice.controller;


import com.tpa.gameservice.dto.NewGameRequest;
import com.tpa.gameservice.model.GameState;
import com.tpa.gameservice.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameServiceController {

    private final GameService gameService;
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createGame(@RequestBody NewGameRequest newGameRequest){
        return gameService.createGame(newGameRequest);
    }
}
