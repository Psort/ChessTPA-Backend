package com.tpa.notificationservice.service;

import com.tpa.notificationservice.model.LogInfo;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log
public class NotificationService {

    @KafkaListener(topics = "notificationTopic")
    public void handleNotification(LogInfo logInfo) {

    }
}
