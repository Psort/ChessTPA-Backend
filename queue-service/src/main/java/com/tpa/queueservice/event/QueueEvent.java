package com.tpa.queueservice.event;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueEvent {
    private String username;
    private Double eloRating;
}
