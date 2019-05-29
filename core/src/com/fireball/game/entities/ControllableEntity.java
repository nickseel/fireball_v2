package com.fireball.game.entities;

import com.fireball.game.entities.player.Ability;

public abstract class ControllableEntity extends Entity {
    protected double moveX, moveY;
    protected double targetX, targetY;
    protected Ability[] abilities;
    protected double[][] abilityCooldowns;

    public ControllableEntity(Team team) {
        super(team);
    }

    public ControllableEntity(Team team, double x, double y) {
        super(team, x, y);
    }
}
