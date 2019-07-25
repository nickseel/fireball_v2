package com.fireball.game.entities.enemies.ai;

import com.fireball.game.entities.ControllableEntity;

public class DummyAI extends AI {
    @Override
    public void run(ControllableEntity entity, double delta) {
        entity.cycleAbilityInputs();
        entity.setMove(0, 0);
    }
}
