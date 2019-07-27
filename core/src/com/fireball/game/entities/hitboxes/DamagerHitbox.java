package com.fireball.game.entities.hitboxes;

import com.fireball.game.entities.Entity;
import com.fireball.game.entities.Team;

public abstract class DamagerHitbox extends Hitbox {
    private boolean damageOverTime = false;

    public DamagerHitbox(Entity owner, Team team, double x, double y, double radius) {
        super(owner, team, x, y, radius);
    }

    public abstract void damageBody(BodyHitbox other);

    public boolean isDamageOverTime() {
        return damageOverTime;
    }

    public void setDamageOverTime(boolean damageOverTime) {
        this.damageOverTime = damageOverTime;
    }
}
