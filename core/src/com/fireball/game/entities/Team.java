package com.fireball.game.entities;

public enum Team {
    PLAYER (1),
    ENEMY (-1),
    NEUTRAL (0);

    public int i;
    Team(int i) {
        this.i = i;
    }

    public boolean collidesWidth(Team other) {
        return i*other.i <= 0;
    }
}
