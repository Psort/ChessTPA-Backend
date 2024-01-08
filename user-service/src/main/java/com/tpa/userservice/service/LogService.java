package com.tpa.userservice.service;


import com.tpa.userservice.event.LogEvent;
import com.tpa.userservice.type.LogType;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LogService {
    private final KafkaTemplate<String, LogEvent> kafkaTemplate;

    public void send(LogType type, String message) {
        String serviceName = "AccessControl";
        kafkaTemplate.send(
                "logManagementTopic",
                LogEvent.builder()
                .serviceName(serviceName)
                .type(type).message(message)
                .build() );

    }
}
