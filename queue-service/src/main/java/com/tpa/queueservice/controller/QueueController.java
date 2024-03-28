package com.tpa.queueservice.controller;


import com.tpa.queueservice.dto.QueueRequest;
import com.tpa.queueservice.service.PlayerConsumer;
import com.tpa.queueservice.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {
    private final QueueService queueService;

    private final PlayerConsumer playerConsumer;
    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> joinUnLimitedGame(@RequestBody QueueRequest queueRequest) {
        String gameId = queueService.addToQueue(queueRequest);
        return ResponseEntity.ok(gameId);
    }

    @PostMapping("/leave")
    @ResponseStatus(HttpStatus.OK)
    public void leaveQueue(@RequestBody QueueRequest queueRequest) {
        playerConsumer.leaveQueue(queueRequest);
    }
}
