package com.fireball.game.entities.player;

import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.Entity;
import com.fireball.game.entities.abilities.Ability;
import com.fireball.game.entities.hitboxes.BodyHitbox;
import com.fireball.game.entities.hitboxes.DamagerHitbox;
import com.fireball.game.rendering.fire.FireRenderer;
import com.fireball.game.util.Util;

import static java.lang.Math.floor;

public class FlamethrowerProjectile extends Ability {
    private final float fadeoutTime = 0.15f;
    private float alpha = 1;

    private boolean isAlive = true;
    private double lifetime;

    private double minRadius, maxRadius, currentRadius, sizeTimer, sizeTimerMax;
    private double angle, velocity, accel;
    private final double collisionRadiusFactor = 0.5;

    private DamagerHitbox hitbox;

    public FlamethrowerProjectile(ControllableEntity owner, Entity castOwner, String subAbilityName,
                          double x, double y, double lifetime, double angle, double velocity, double accel,
                          double minRadius, double maxRadius, double sizeTimerMax) {
        super("flamethrower projectile", owner, castOwner, subAbilityName, x, y);
        this.lifetime = lifetime;
        this.angle = angle;
        this.velocity = velocity;
        this.accel = accel;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.currentRadius = minRadius;
        this.sizeTimer = sizeTimerMax;
        this.sizeTimerMax = sizeTimerMax;
        terrainCollisionRadius = currentRadius*collisionRadiusFactor;

        hitbox = new DamagerHitbox(this, team, x, y, currentRadius) {
            @Override
            public void damage(BodyHitbox other) {
                other.takeDamage(1, 1, 0);
            }
        };

        registerEntityAndHitboxes();
    }

    @Override
    public void updatePre(double delta) {
        if(sizeTimer > 0) {
            sizeTimer = Math.max(0, sizeTimer - delta);
            currentRadius = Util.mix(maxRadius, minRadius, sizeTimer / sizeTimerMax);
            terrainCollisionRadius = currentRadius*collisionRadiusFactor;

            hitbox.setRadius(currentRadius);
        }

        velocity = Math.max(0, velocity + accel * delta);
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
    }

    @Override
    public void updatePost(double delta) {
        lifetime -= delta;

        alpha = (float)Math.max(0, Math.min(1, lifetime / fadeoutTime));

        if(lifetime < 0) {
            kill();
        }
    }

    @Override
    public void drawFire(FireRenderer renderer) {
        renderer.drawFire((float)x, (float)y, (float)currentRadius*1, 0.25f * alpha);
    }

    @Override
    public void eventTerrainCollision(double angle) {
        xVel = Math.cos(this.angle) * velocity;
        yVel = Math.sin(this.angle) * velocity;
        xVel *= Math.cos(angle);
        yVel *= Math.sin(angle);
        velocity = Math.hypot(xVel, yVel);
        this.angle = Math.atan2(yVel, xVel);
        lifetime = Math.min(0.2, lifetime);
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public void kill() {
        isAlive = false;
    }

    @Override
    public void updateSlotPositions(double slotSize) {
        slotMinX = (int)floor((x - currentRadius) / slotSize);
        slotMaxX = (int)floor((x + currentRadius) / slotSize);
        slotMinY = (int)floor((y - currentRadius) / slotSize);
        slotMaxY = (int)floor((y + currentRadius) / slotSize);
    }
}
