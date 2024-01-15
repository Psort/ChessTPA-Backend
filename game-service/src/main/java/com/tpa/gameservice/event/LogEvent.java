package com.tpa.gameservice.event;


import com.tpa.gameservice.type.LogType;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class LogEvent {
    private String serviceName;
    private LogType type;
    private String message;
    private ZonedDateTime timestamp;
}
