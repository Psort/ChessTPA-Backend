package com.tpa.queueservice.event;


import com.tpa.queueservice.type.LogType;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Builder
public class LogEvent {
    private final String serviceName;
    private final LogType type;
    private final String message;
    private final ZonedDateTime timestamp;
}
