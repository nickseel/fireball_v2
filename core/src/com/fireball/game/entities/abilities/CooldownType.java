package com.fireball.game.entities.abilities;

public enum CooldownType {
    NORMAL,
    RING_OBJECTS_DESTROYED,
    RING_FAILSAFE;

    public static final double RING_FAILSAFE_COOLDOWN = 30;
}
