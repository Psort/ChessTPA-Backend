package com.tpa.chessengine.service;


import com.tpa.chessengine.event.LogEvent;
import com.tpa.chessengine.type.LogType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Service
public class LogService {
    private final KafkaTemplate<String, LogEvent> kafkaTemplate;

    private final  String SERVICENAME = "ChessEngine ";

    public void sendError(String message, Object... args) {
        send(LogType.ERROR , message , args);
    }
    public void sendInfo(String message, Object... args) {
        send(LogType.INFO , message , args);
    }
    public void sendWarning(String message, Object... args) {
        send(LogType.WARN , message , args);
    }

    private void send(LogType type, String message, Object... args) {
        String formattedMessage = String.format(message, args);

        kafkaTemplate.send(
                "logManagementTopic",
                LogEvent.builder()
                        .serviceName(SERVICENAME)
                        .type(type)
                        .message(formattedMessage)
                        .timestamp(ZonedDateTime.now(ZoneId.of("Europe/Warsaw")))
                        .build());
    }
}
