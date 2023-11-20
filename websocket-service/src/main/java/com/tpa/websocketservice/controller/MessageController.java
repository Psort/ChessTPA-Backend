package com.tpa.websocketservice.controller;

import com.tpa.websocketservice.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final WebSocketService webSocketService;
    @MessageMapping("/game/{gameId}")
    @SendTo("/topic/messages/{gameId}")
    public String getMessages(@DestinationVariable String gameId) {
        return webSocketService.getGame(gameId);
    }
}