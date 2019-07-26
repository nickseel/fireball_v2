package com.fireball.game.entities.player;

import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.Entity;
import com.fireball.game.entities.abilities.Ability;
import com.fireball.game.entities.abilities.AbilityCooldown;
import com.fireball.game.entities.abilities.CastArgumentOverride;
import com.fireball.game.entities.hitboxes.BodyHitbox;
import com.fireball.game.entities.hitboxes.DamagerHitbox;
import com.fireball.game.rendering.fire.FireRenderer;
import com.fireball.game.util.Util;

import static java.lang.Math.floor;

public class RingFireball extends Ability {
    protected boolean isAlive = true;
    protected double radius;
    protected double lifetime, maxLifetime;
    protected double angleOffset, spinSpeed, currentAngle;
    protected double minDistance, maxDistance, currentDistance, extendTime;

    protected AbilityCooldown cooldownReference;

    protected DamagerHitbox damageHitbox;
    protected BodyHitbox bodyHitbox;

    public RingFireball(ControllableEntity owner, Entity castOwner, String subAbilityName, AbilityCooldown cooldownReference,
                        double x, double y, double radius, double lifetime, double angleOffset, double spinSpeed,
                        double minDistance, double maxDistance, double extendTime) {
        super("fireball", owner, castOwner, subAbilityName, x, y);
        this.cooldownReference = cooldownReference;
        this.radius = radius;
        this.lifetime = lifetime;
        this.maxLifetime = lifetime;
        this.angleOffset = angleOffset;
        this.spinSpeed = spinSpeed;
        this.currentAngle = angleOffset;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.currentDistance = minDistance;
        this.extendTime = extendTime;

        damageHitbox = new DamagerHitbox(this, team, x, y, radius) {
            @Override
            public void damage(BodyHitbox other) {
                other.takeDamage(1, 1, 0, 0, 1);
                kill();
            }
        };

        bodyHitbox = new BodyHitbox(this, team, x, y, radius) {
            @Override
            public void takeDamage(double damage, double knockback, double knockbackAngle, double stun, double stunFriction) {
                kill();
            }

            @Override
            public void getPushedBy(BodyHitbox other) {
                //unpushable
            }
        };
        bodyHitbox.setPushable(false);


        damagerHitboxes = new DamagerHitbox[] {damageHitbox};
        bodyHitboxes = new BodyHitbox[] {bodyHitbox};
        registerEntityAndHitboxes();
    }

    @Override
    public void updatePre(double delta) {
        currentDistance = Util.mix(minDistance, maxDistance, Math.pow(Math.min(1, (maxLifetime - lifetime) / extendTime), 0.3));

        currentAngle += spinSpeed * delta;
        nextX = castOwner.getX() + currentDistance * Math.cos(currentAngle);
        nextY = castOwner.getY() + currentDistance * Math.sin(currentAngle);
    }

    @Override
    public void updateMid(double delta) {
        x = nextX;
        y = nextY;
        bodyHitbox.setPosition(x, y);
        damageHitbox.setPosition(x, y);
    }

    @Override
    public void updatePost(double delta) {
        lifetime -= delta;

        if(lifetime <= 0) {
            kill();
        }
    }

    @Override
    public void drawFire(FireRenderer renderer) {
        renderer.drawFire((float)x, (float)y, (float)radius*1, 0.75f);
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
        if(isAlive) {
            if(cooldownReference != null)
                cooldownReference.modifyValue(-1);
        }

        isAlive = false;
        castSubAbility(new CastArgumentOverride(CastArgumentOverride.ARGUMENT_OTHER).setOther(new Object[] {"radius", radius}));
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
