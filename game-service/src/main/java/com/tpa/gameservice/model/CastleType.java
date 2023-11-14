package com.tpa.gameservice.model;

public enum CastleType {
    SHORTWHITE("K"),
    LONGWHITE("Q"),
    SHORTBLACK("k"),
    LONGBLACK("q"),
    ;

    private final String value;

    CastleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}