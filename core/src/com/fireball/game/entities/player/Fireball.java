package com.fireball.game.entities.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.Entity;
import com.fireball.game.entities.abilities.Ability;
import com.fireball.game.entities.hitboxes.BodyHitbox;
import com.fireball.game.entities.hitboxes.DamagerHitbox;
import com.fireball.game.rendering.fire.FireRenderer;
import com.fireball.game.util.Util;

import static java.lang.Math.floor;

public class Fireball extends Ability {
    protected boolean isAlive = true;
    protected double radius;
    protected double angle;
    protected double velocity;

    protected DamagerHitbox hitbox;

    public Fireball(ControllableEntity owner, Entity castOwner, String subAbilityName,
                    double x, double y, double radius, double angle, double velocity) {
        super("fireball", owner, castOwner, subAbilityName, x, y);
        this.radius = radius;
        this.angle = angle;
        this.velocity = velocity;
        terrainCollisionRadius = radius;

        hitbox = new DamagerHitbox(this, team, x, y, radius) {
            @Override
            public void damage(BodyHitbox other) {
                other.takeDamage(1, 1, 0);
                kill();
            }
        };

        damagerHitboxes = new DamagerHitbox[] {hitbox};
        registerEntityAndHitboxes();
    }

    @Override
    public void updatePre(double delta) {
        xVel = Math.cos(angle) * velocity;
        yVel = Math.sin(angle) * velocity;

        //set next position for collision detection
        nextX = x + (xVel) * delta;
        nextY = y + (yVel) * delta;
    }

    @Override
    public void updateMid(double delta) {
        x = nextX;
        y = nextY;

        hitbox.setPosition(x, y);
    }

    @Override
    public void updatePost(double delta) {

    }

    @Override
    public void drawFire(FireRenderer renderer) {
        renderer.drawFire((float)x, (float)y, (float)radius*1, 1.0f);
    }

    @Override
    public void eventTerrainCollision(double angle) {
        kill();
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public void kill() {
        isAlive = false;
        castSubAbility();
        subAbilityName = null;
    }

    @Override
    public void updateSlotPositions(double slotSize) {
        slotMinX = (int)floor((x - radius) / slotSize);
        slotMaxX = (int)floor((x + radius) / slotSize);
        slotMinY = (int)floor((y - radius) / slotSize);
        slotMaxY = (int)floor((y + radius) / slotSize);
    }
}
