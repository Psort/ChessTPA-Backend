package com.tpa.logsmanagementservice.event;

import com.tpa.logsmanagementservice.type.LogType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogEvent {
    private String serviceName;
    private LogType type;
    private String message;
}
