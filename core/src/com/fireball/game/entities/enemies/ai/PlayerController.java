package com.fireball.game.entities.enemies.ai;

import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.player.PlayerData;
import com.fireball.game.input.ControlMapping;
import com.fireball.game.input.InputManager;

import java.util.LinkedList;

public class PlayerController extends AI {
    @Override
    public void run(ControllableEntity entity, double delta) {
        LinkedList<Double[]> heldKeys = InputManager.getHeldKeys();
        int[] abilityKeys = PlayerData.getAbilityKeys();
        double moveX = 0, moveY = 0;

        entity.cycleAbilityInputs();

        for(Double[] keys: heldKeys) {
            //System.out.println(keys[0] + " " + keys[1]);
            if(keys[0] == ControlMapping.MOVE_LEFT)
                moveX--;
            if(keys[0] == ControlMapping.MOVE_RIGHT)
                moveX++;
            if(keys[0] == ControlMapping.MOVE_UP)
                moveY--;
            if(keys[0] == ControlMapping.MOVE_DOWN)
                moveY++;
        }

        for(int i = 0; i < abilityKeys.length; i++) {
            entity.setAbilityInput(i, false);
            for(Double[] keys: heldKeys) {
                if(keys[0] == abilityKeys[i])
                    entity.setAbilityInput(i, true);
            }
        }
        entity.setMove(moveX, moveY);
    }
}
