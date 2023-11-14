package com.tpa.userservice.controller;

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
    public void create(@RequestBody SignUpRequest request){
        userService.createUser(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> getUsernameByEmail(@RequestParam String email){
        return userService.getUserByEmail(email);
    }

    @PostMapping("/game")
    @ResponseStatus(HttpStatus.OK)
    public void addGame(@RequestBody NewGameRequest request){
        userService.addGame(request);
    }
}
