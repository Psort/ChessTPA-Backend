package com.tpa.logsmanagementservice.service;

import com.tpa.logsmanagementservice.event.LogEvent;
import com.tpa.logsmanagementservice.type.LogType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class LogConsumer {

    private final LogManagementService logManagementService;
    private static final Logger logger = LogManager.getLogger(LogConsumer.class);
    @KafkaListener(topics = "logManagementTopic",groupId = "logManagementId")
    public void handleNotification(LogEvent log) {
        showLog(log);
        logManagementService.saveLog(log);
    }
    private void showLog(LogEvent logEvent){
        switch (logEvent.getType()) {
            case ERROR:
                logger.error(logEvent.getMessage());
                break;
            case WARN:
                logger.warn(logEvent.getMessage());
                break;
            default:
                logger.info(logEvent.getMessage());
        }
    }
}
