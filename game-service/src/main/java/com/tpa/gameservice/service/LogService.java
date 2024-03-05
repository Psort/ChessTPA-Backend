package com.tpa.gameservice.service;


import com.tpa.gameservice.event.LogEvent;
import com.tpa.gameservice.type.LogType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Service
public class LogService {
    private final KafkaTemplate<String, LogEvent> kafkaTemplate;

    public void send(LogType type, String message) {
        String serviceName = "GameService";
        kafkaTemplate.send(
                "logManagementTopic",
                LogEvent.builder()
                .serviceName(serviceName)
                .type(type).message(message)
                .timestamp(ZonedDateTime.now(ZoneId.of("Europe/Warsaw")))
                .build());

    }
}
