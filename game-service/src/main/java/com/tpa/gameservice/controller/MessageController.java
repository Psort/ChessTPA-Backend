package com.tpa.gameservice.controller;

import com.tpa.gameservice.dto.GameResponse;
import com.tpa.gameservice.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final GameService gameService;
    @MessageMapping("/game")
    @SendTo("/topic/messages")
    public GameResponse getMessages(String gameId){
        System.out.println("RECIEVED MESSAGE");
       return gameService.getGame(gameId);
    }
}
