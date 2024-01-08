package com.tpa.useraccessservice.event;

import com.tpa.useraccessservice.type.LogType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogEvent {
    private String serviceName;
    private LogType type;
    private String message;
}
