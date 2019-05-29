package com.fireball.game.entities.player;

import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.Entity;
import com.fireball.game.entities.abilities.Ability;
import com.fireball.game.entities.hitboxes.BodyHitbox;
import com.fireball.game.entities.hitboxes.DamagerHitbox;
import com.fireball.game.rendering.fire.FireRenderer;

import static java.lang.Math.floor;

public class Explosion extends Ability {
    protected boolean isAlive = true;
    protected double radius;

    protected DamagerHitbox hitbox;

    public Explosion(ControllableEntity owner, Entity castOwner, String subAbilityName,
                    double x, double y, double radius) {
        super("fireball", owner, castOwner, subAbilityName, x, y);
        this.radius = radius;
        terrainCollisionRadius = radius;

        hitbox = new DamagerHitbox(this, team, x, y, radius) {
            @Override
            public void damage(BodyHitbox other) {
                other.takeDamage(1, 1, 0);
            }
        };

        damagerHitboxes = new DamagerHitbox[] {hitbox};
        registerEntityAndHitboxes();
    }

    @Override
    public void updatePre(double delta) {
        //set next position for collision detection
        nextX = x;
        nextY = y;
    }

    @Override
    public void updatePost(double delta) {
        hitbox.setPosition(x, y);
        kill(); //only lasts for one frame
    }

    @Override
    public void drawFire(FireRenderer renderer) {
        renderer.drawFire((float)x, (float)y, (float)radius*1, 1.0f);
    }

    @Override
    public void eventTerrainCollision(double angle) {

    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public void kill() {
        isAlive = false;
        castSubAbility();
    }

    @Override
    public void updateSlotPositions(double slotSize) {
        slotMinX = (int)floor((x - radius) / slotSize);
        slotMaxX = (int)floor((x + radius) / slotSize);
        slotMinY = (int)floor((y - radius) / slotSize);
        slotMaxY = (int)floor((y + radius) / slotSize);
    }
}
