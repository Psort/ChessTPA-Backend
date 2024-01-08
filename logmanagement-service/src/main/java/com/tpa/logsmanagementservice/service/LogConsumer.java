package com.tpa.logsmanagementservice.service;

import com.tpa.logsmanagementservice.event.LogEvent;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LogConsumer {

    private final LogManagementService logManagementService;
    @KafkaListener(topics = "logManagementTopic",groupId = "logManagementId")
    public void handleNotification(LogEvent log) {
        System.out.println(log.getType());
        logManagementService.saveLog(log);
    }

}
