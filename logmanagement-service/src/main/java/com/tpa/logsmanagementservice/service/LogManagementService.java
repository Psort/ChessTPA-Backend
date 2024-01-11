package com.tpa.logsmanagementservice.service;


import com.tpa.logsmanagementservice.event.LogEvent;
import com.tpa.logsmanagementservice.model.Log;
import com.tpa.logsmanagementservice.repository.LogManagementRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;




@Service
@AllArgsConstructor

public class LogManagementService {
    private final LogManagementRepository logManagementRepository;
    public void saveLog(LogEvent logEvent) {
        
        Log log = Log.builder()
                .message(logEvent.getMessage())
                .serviceName(logEvent.getServiceName())
                .type(logEvent.getType())
                .timestamp(logEvent.getTimestamp())
                .build();

        logManagementRepository.save(log);
    }
}
