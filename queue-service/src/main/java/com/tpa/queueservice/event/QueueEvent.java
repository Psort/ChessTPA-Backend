package com.tpa.queueservice.event;


import com.tpa.queueservice.type.LogType;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class QueueEvent {
    private String username;
    private     Double eloRating;
}
