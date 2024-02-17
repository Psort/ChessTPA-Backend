package com.tpa.queueservice.dto;

import com.tpa.queueservice.type.QueueType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QueueRequest {
    String username;
    Double eloRating;
    QueueType queueType;
}
