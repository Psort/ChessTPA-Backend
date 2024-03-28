package com.tpa.websocketservice.controller;

import com.tpa.websocketservice.dto.MessageRequest;
import com.tpa.websocketservice.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final WebSocketService webSocketService;
    @MessageMapping("/game/{gameId}")
    @SendTo("/topic/game/{gameId}")
    public String getGameId(@DestinationVariable String gameId) {
        return webSocketService.getGame(gameId);
    }

    @MessageMapping("/messages/{gameId}")
    @SendTo("/topic/messages/{gameId}")
    public String processMessageFromClient(@RequestBody MessageRequest messageRequest) {
        return webSocketService.getMessages(messageRequest);
    }


}