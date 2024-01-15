package com.tpa.queueservice.event;


import com.tpa.queueservice.type.LogType;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Builder
public class LogEvent {
    private String serviceName;
    private LogType type;
    private String message;
    private ZonedDateTime timestamp;
}
