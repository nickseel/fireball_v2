package com.fireball.game.entities.enemies.ai;

import com.fireball.game.entities.ControllableEntity;

public abstract class AI {
    public abstract void run(ControllableEntity entity, double delta);
}
