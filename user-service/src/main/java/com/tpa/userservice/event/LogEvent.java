package com.tpa.userservice.event;


import com.tpa.userservice.type.LogType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogEvent {
    private String serviceName;
    private LogType type;
    private String message;
}
