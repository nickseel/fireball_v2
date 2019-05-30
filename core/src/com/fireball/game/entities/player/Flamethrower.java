package com.fireball.game.entities.player;

import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.Entity;
import com.fireball.game.entities.abilities.Ability;
import com.fireball.game.entities.abilities.CastArgumentOverride;

import static java.lang.Math.floor;

public class Flamethrower extends Ability {
    private boolean isAlive = true;
    private double angle, angleRange;
    private double fireRate, fireTimer;
    private int numProjectiles;

    public Flamethrower(ControllableEntity owner, Entity castOwner, String subAbilityName,
                    double x, double y, double angle, double angleRange, double fireRate, int numProjectiles) {
        super("flamethrower", owner, castOwner, subAbilityName, x, y);
        this.angle = angle;
        this.angleRange = angleRange;
        this.fireRate = fireRate;
        this.fireTimer = 1 / fireRate;
        this.numProjectiles = numProjectiles;

        registerEntityAndHitboxes();
    }

    @Override
    public void updatePre(double delta) {
        nextX = x;
        nextY = y;
    }

    @Override
    public void updateMid(double delta) {

    }

    @Override
    public void updatePost(double delta) {
        x = castOwner.getX();
        y = castOwner.getY();

        if(!castOwner.isAlive() || !owner.isAlive() || !owner.abilitiesStreaming()) {
            kill();
        }

        if(isAlive) {
            fireTimer += delta;
            while(fireTimer > 1/fireRate) {
                fireTimer -= 1/fireRate;

                for(int i = 0; i < numProjectiles; i++) {
                    double flameAngle = angle + angleRange*((i/(numProjectiles/2))-1) + Math.random()*(2*angleRange/numProjectiles);

                    castSubAbility(new CastArgumentOverride(CastArgumentOverride.ARGUMENT_ANGLE).setAngle(flameAngle));
                }
            }
        }
    }

    @Override
    public void eventTerrainCollision(double angle) {}

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public void kill() {
        isAlive = false;
    }

    @Override
    public void updateSlotPositions(double slotSize) {}
}
