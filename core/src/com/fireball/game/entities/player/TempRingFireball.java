package com.fireball.game.entities.player;

import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.Entity;
import com.fireball.game.entities.abilities.AbilityCooldown;
import com.fireball.game.entities.abilities.CastArgumentOverride;

public class TempRingFireball extends RingFireball {

    public TempRingFireball(ControllableEntity owner, Entity castOwner, String subAbilityName, AbilityCooldown cooldownReference, double x, double y, double radius, double lifetime, double angleOffset, double spinSpeed, double minDistance, double maxDistance, double extendTime) {
        super(owner, castOwner, subAbilityName, cooldownReference, x, y, radius, lifetime, angleOffset, spinSpeed, minDistance, maxDistance, extendTime);
    }

    @Override
    public void kill() {
        if(isAlive) {
            if(cooldownReference != null)
                cooldownReference.modifyValue(-1);
        }

        isAlive = false;
        castSubAbility(new CastArgumentOverride(CastArgumentOverride.ARGUMENT_OTHER).setOther(new Object[] {"radius", radius}));
        subAbilityName = null;
    }
}
