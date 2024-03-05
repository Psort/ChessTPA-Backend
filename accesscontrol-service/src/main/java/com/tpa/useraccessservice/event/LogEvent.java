package com.tpa.useraccessservice.event;

import com.tpa.useraccessservice.type.LogType;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class LogEvent {
    private final String serviceName;
    private final LogType type;
    private final String message;
    private final ZonedDateTime timestamp;
}
