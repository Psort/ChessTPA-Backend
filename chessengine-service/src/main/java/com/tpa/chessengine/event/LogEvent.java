package com.tpa.chessengine.event;


import com.tpa.chessengine.type.LogType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogEvent {
    private String serviceName;
    private LogType type;
    private String message;
}
