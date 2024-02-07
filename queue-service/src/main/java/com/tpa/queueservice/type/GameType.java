package com.tpa.queueservice.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GameType {
    ONE(1),
    THREE(3),
    FIVE(5),
    TEN(10),
    INFINITE(0);
    private final int minutes;

}