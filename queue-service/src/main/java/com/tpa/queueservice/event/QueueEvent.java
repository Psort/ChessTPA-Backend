package com.tpa.queueservice.event;

import com.tpa.queueservice.type.GameType;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueEvent {
    private String username;
    private Double eloRating;
    private GameType gameType;
}
