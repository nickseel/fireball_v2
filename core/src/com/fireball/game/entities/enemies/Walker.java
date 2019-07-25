package com.fireball.game.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.Team;
import com.fireball.game.entities.abilities.AbilityType;
import com.fireball.game.entities.enemies.ai.DummyAI;
import com.fireball.game.entities.hitboxes.DamagerHitbox;
import com.fireball.game.rendering.fire.FireRenderer;
import com.fireball.game.rooms.rooms.RoomCamera;
import com.fireball.game.entities.hitboxes.BodyHitbox;
import com.fireball.game.input.ControlMapping;
import com.fireball.game.input.InputManager;
import com.fireball.game.util.DataFile;
import com.fireball.game.util.Util;

import java.util.LinkedList;

import static java.lang.Math.*;

public class Walker extends ControllableEntity {
    private BodyHitbox hitbox;
    private DamagerHitbox hurtbox;

    private double healthRegen;
    private double radius = 12;

    private double maxPushSpeed = 200;
    private double pushVelX = 0;
    private double pushVelY = 0;

    public Walker(int x, int y) {
        super(Team.PLAYER, "player", x, y, new AbilityType[0], 1, new DummyAI());

        DataFile.setCurrentLocation("entities", "walker");
        this.maxHealth = DataFile.getFloat("maxHealth"); this.health = maxHealth;
        this.healthRegen = DataFile.getFloat("healthRegen");
        this.radius = DataFile.getFloat("radius");
        this.accel = DataFile.getFloat("accel");
        this.friction = DataFile.getFloat("friction");
        this.maxSpeed = DataFile.getFloat("maxSpeed");
        this.turnAssist = DataFile.getFloat("turnAssist");

        this.terrainCollisionRadius = radius;

        hitbox = new BodyHitbox(this, team, x, y, radius) {
            @Override
            public void takeDamage(double damage, double knockback, double knockbackAngle) {
                health -= damage;
                xVel += knockback * Math.cos(knockbackAngle);
                yVel += knockback * Math.sin(knockbackAngle);
            }

            @Override
            public void getPushedBy(BodyHitbox other) {
                double angle = Math.atan2(other.getY() - y, other.getX() - x);
                double normalX = Math.cos(angle);
                double normalY = Math.sin(angle);

                double distance = Math.hypot(other.getY() - y, other.getX() - x);
                double pushPct = Math.max(0, Math.min(1, Util.mix(1.0, -0.05, Math.pow(distance / (radius + other.getRadius()), 3))));

                pushVelX -= maxPushSpeed * pushPct * normalX;
                pushVelY -= maxPushSpeed * pushPct * normalY;
            }
        };
        bodyHitboxes = new BodyHitbox[] {hitbox};


        hurtbox = new DamagerHitbox(this, team, x, y, radius) {
            @Override
            public void damage(BodyHitbox other) {
                other.takeDamage(1, 1, 0);
            }
        };
        damagerHitboxes = new DamagerHitbox[] {hurtbox};

        registerEntityAndHitboxes();
    }

    ////////////////////////////////////////////////////
    //              ENTITY UPDATE ORDER:              //
    //   1. Entities Push Each Other                  //
    //   2. [Update Pre]                              //
    //   3. Entities Collide With Terrain             //
    //   4. [Update Mid]                              //
    //   5. Entity Hitboxes Collide With Each Other   //
    //   6. [Update Post]                             //
    //   7. Dead Entities Are Kill()ed                //
    //   8. New Entities Added                        //
    ////////////////////////////////////////////////////

    @Override
    public void updatePre(double delta) {
        LinkedList<Double[]> heldKeys = InputManager.getHeldKeys();

        //check input
        moveX = 0;
        moveY = 0;
        // RUN AI HERE

        updateAbilities(delta);
        move(delta);

        //set next position for collision detection
        nextX = x + (xVel + pushVelX) * delta;
        nextY = y + (yVel + pushVelY) * delta;

        pushVelX = 0;
        pushVelY = 0;
    }

    @Override
    public void updateMid(double delta) {
        x = nextX;
        y = nextY;

        hitbox.setPosition(x, y);
        hurtbox.setPosition(x, y);
    }

    @Override
    public void updatePost(double delta) {

    }

    @Override
    public void eventTerrainCollision(double angle) {

    }

    @Override
    public void updateSlotPositions(double slotSize) {
        slotMinX = (int)floor((x - radius) / slotSize);
        slotMaxX = (int)floor((x + radius) / slotSize);
        slotMinY = (int)floor((y - radius) / slotSize);
        slotMaxY = (int)floor((y + radius) / slotSize);
    }
}
