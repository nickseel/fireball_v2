package com.fireball.game.util;

public enum EfficiencyMetricType {
    ENTIRE_FRAME        ("ENTIRE FRAME"),
    UPDATE_ALL          ("UPDATE ALL"),
    DRAW_ALL            ("DRAW ALL"),
    UPDATE_ENTITIES     ("UPDATE ENTITIES"),
    DRAW_ENTITIES       ("DRAW ENTITIES"),
    DRAW_WALLS          ("DRAW WALLS"),
    COLLISION           ("COLLISION");

    private String name;
    EfficiencyMetricType(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
}
