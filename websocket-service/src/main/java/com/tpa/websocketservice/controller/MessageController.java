package com.tpa.websocketservice.controller;

import com.tpa.websocketservice.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final WebSocketService webSocketService;
    @MessageMapping("/game")
    @SendTo("/topic/messages")
    public String getMessages(String gameId){
        return webSocketService.getGame(gameId);
    }
}