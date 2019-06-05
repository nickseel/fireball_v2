package com.fireball.game.entities.player;

import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.Entity;
import com.fireball.game.entities.EntityManager;
import com.fireball.game.entities.abilities.Ability;
import com.fireball.game.entities.hitboxes.DamagerHitbox;
import com.fireball.game.rendering.fire.FireRenderer;
import com.fireball.game.rendering.textures.TextureData;
import com.fireball.game.rendering.textures.TextureManager;
import com.fireball.game.util.Util;

import static java.lang.Math.floor;

public class Laser extends Ability {
    protected static final double STEP_SIZE = 2;
    protected static final double MAX_DISTANCE = 9999;
    protected boolean isAlive = true;
    protected double radius;

    protected double currentMaxDistance, maxDistanceExtendSpeed;
    protected double currentAngle, targetAngle;

    protected DamagerHitbox damageHitbox;

    protected boolean collided;

    public Laser(ControllableEntity owner, Entity castOwner, String subAbilityName,
                 double x, double y, double radius, double extendSpeed) {
        super("laser", owner, castOwner, subAbilityName, x, y);

        this.radius = radius;
        this.currentMaxDistance = 0;
        this.maxDistanceExtendSpeed = extendSpeed;
        this.currentAngle = 0;
        this.targetAngle = 0;
        terrainCollisionRadius = radius/2;



        registerEntityAndHitboxes();
    }

    @Override
    public void updatePre(double delta) {
        currentMaxDistance = Math.min(MAX_DISTANCE, currentMaxDistance + maxDistanceExtendSpeed * delta);

        targetAngle = Math.atan2(owner.getTargetY() - owner.getY(), owner.getTargetX() - owner.getX());
        currentAngle = targetAngle;
        x = owner.getX();
        y = owner.getY();
        nextX = x;
        nextY = y;

        collided = false;
    }

    @Override
    public void updateMid(double delta) {

    }

    @Override
    public void updatePost(double delta) {
        if(!owner.abilitiesStreaming() || !owner.isAlive()) {
            kill();
        }

        double dist, prevDist = 0;
        for(dist = Math.min(STEP_SIZE, currentMaxDistance)-0.01; dist < currentMaxDistance; dist += STEP_SIZE) {
            dist = Math.min(dist, currentMaxDistance);

            x = owner.getX() + prevDist * Math.cos(currentAngle);
            y = owner.getY() + prevDist * Math.sin(currentAngle);
            nextX = owner.getX() + dist * Math.cos(currentAngle);
            nextY = owner.getY() + dist * Math.sin(currentAngle);

            EntityManager.current.collideIndividualEntity(this);
            if(collided)
                break;

            prevDist = dist;
        }
        x = nextX;
        y = nextY;
    }

    @Override
    public void drawFire(FireRenderer renderer) {
        float fireLevel = 0.4f;
        float dist = (float)Math.hypot(x-owner.getX(), y-owner.getY());

        renderer.drawFireTexture(
                TextureManager.getTexture(TextureData.LASER_END),
                (float)x,
                (float)y,
                (float)radius*4, (float)Math.min(dist, radius*4),
                (float)Math.toDegrees(currentAngle)+90f, fireLevel);
        renderer.drawFireTexture(
                TextureManager.getTexture(TextureData.LASER_END),
                (float)owner.getX(),
                (float)owner.getY(),
                (float)radius*4, (float)Math.min(dist, radius*4),
                (float)Math.toDegrees(currentAngle)-90f, fireLevel);

        if(dist > radius*4)
            renderer.drawFireTexture(
                    TextureManager.getTexture(TextureData.LASER),
                    (float)(x + owner.getX())/2,
                    (float)(y + owner.getY())/2,
                    (float)radius*4, dist - (float)radius*4,
                    (float)Math.toDegrees(currentAngle)+90f, fireLevel);
    }

    @Override
    public void eventTerrainCollision(double angle) {
        collided = true;
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
