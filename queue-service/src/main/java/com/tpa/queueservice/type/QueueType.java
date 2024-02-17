package com.tpa.queueservice.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QueueType {
    ONEMINQUEUE  ("one-min-queue"),
    THREEMINQUEUE  ("three-min-queue"),
    FIVEMINQUEUE  ("five-min-queue"),
    TENMINQUEUE  ("ten-min-queue"),
    UNLIMITEDQUEUE  ("unLimited-queue");

    private final String queueStringType;


}
