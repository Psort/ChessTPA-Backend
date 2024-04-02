package com.tpa.userservice.controller;

import com.tpa.userservice.dto.EloTradeRequest;
import com.tpa.userservice.dto.NewGameRequest;
import com.tpa.userservice.dto.SignUpRequest;

import com.tpa.userservice.dto.UserResponse;
import com.tpa.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody SignUpRequest request){
        return userService.createUser(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> getUsernameByEmail(@RequestParam String email){
        UserResponse userResponse = userService.getUserByEmail(email);
        return ResponseEntity.ok(userResponse);
    }
    @GetMapping("/elo")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Double> getUserElo(@RequestParam String username){
        Double elo = userService.getUserByElo(username);
        return ResponseEntity.ok(elo);
    }

    @PostMapping("/game")
    @ResponseStatus(HttpStatus.OK)
    public void addGame(@RequestBody NewGameRequest request){
        userService.addGame(request);
    }

    @PatchMapping
    public void calculateElo(@RequestBody EloTradeRequest request){
        userService.tradeEloPoints(request.getWinningUsername(), request.getLosingUsername(), request.isWin());
    }
}
