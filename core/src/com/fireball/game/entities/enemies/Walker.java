package com.fireball.game.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.Team;
import com.fireball.game.entities.abilities.AbilityType;
import com.fireball.game.entities.enemies.ai.DummyAI;
import com.fireball.game.entities.hitboxes.DamagerHitbox;
import com.fireball.game.rendering.fire.FireRenderer;
import com.fireball.game.rendering.shadow.ShadowRenderer;
import com.fireball.game.rendering.textures.TextureManager;
import com.fireball.game.rendering.textures.TextureSheetData;
import com.fireball.game.rooms.rooms.RoomCamera;
import com.fireball.game.entities.hitboxes.BodyHitbox;
import com.fireball.game.input.ControlMapping;
import com.fireball.game.input.InputManager;
import com.fireball.game.util.DataFile;
import com.fireball.game.util.Util;

import java.util.LinkedList;

import static java.lang.Math.*;

public class Walker extends ControllableEntity {
    private TextureRegion[] sprite;
    private BodyHitbox hitbox;
    private DamagerHitbox hurtbox;

    private double radius;

    private double maxPushSpeed = 200;
    private double pushVelX = 0;
    private double pushVelY = 0;

    private double contactDamage, contactKnockback, contactStun, contactStunFriction;

    public Walker(int x, int y) {
        super(Team.ENEMY, "player", x, y, new AbilityType[0], 1, new DummyAI());

        sprite = TextureManager.getTextureSheet(TextureSheetData.WALKER);

        DataFile.setCurrentLocation("entities", "walker");
        this.maxHealth = DataFile.getFloat("maxHealth"); this.health = maxHealth;
        this.radius = DataFile.getFloat("radius");
        //this.accel = DataFile.getFloat("accel");
        this.friction = DataFile.getFloat("friction");
        this.maxSpeed = DataFile.getFloat("maxSpeed");
        this.turnAssist = DataFile.getFloat("turnAssist");
        this.weight = DataFile.getFloat("weight");
        this.contactDamage = DataFile.getFloat("contact_damage");
        this.contactKnockback = DataFile.getFloat("contact_knockback");
        this.contactStun = DataFile.getFloat("contact_stun");
        this.contactStunFriction = DataFile.getFloat("contact_stun_friction");

        this.terrainCollisionRadius = radius;

        hitbox = new BodyHitbox(this, team, x, y, radius) {
            @Override
            public void takeDamage(double damage, double knockback, double knockbackAngle, double stun, double stunFriction_) {
                health -= damage;
                xVel += (knockback * Math.cos(knockbackAngle)) / weight;
                yVel += (knockback * Math.sin(knockbackAngle)) / weight;
                stunTimer += stun;
                stunFriction = stunFriction_;
            }

            @Override
            public void getPushedBy(BodyHitbox other) {
                double otherWeight = 1;
                if(other.getOwner() instanceof ControllableEntity) {
                    ControllableEntity otherC = (ControllableEntity)other.getOwner();
                    otherWeight = otherC.getWeight();
                }

                double weightRatio = otherWeight / weight;
                double angle = Math.atan2(other.getY() - y, other.getX() - x);
                double normalX = Math.cos(angle);
                double normalY = Math.sin(angle);

                double distance = Math.hypot(other.getY() - y, other.getX() - x);
                double pushPct = Math.max(0, Math.min(1, Util.mix(1.0, -0.05, Math.pow(distance / (radius + other.getRadius()), 3))));

                pushVelX -= maxPushSpeed * weightRatio * pushPct * normalX;
                pushVelY -= maxPushSpeed * weightRatio * pushPct * normalY;
            }
        };
        bodyHitboxes = new BodyHitbox[] {hitbox};


        hurtbox = new DamagerHitbox(this, team, x, y, radius) {
            @Override
            public void damage(BodyHitbox other) {
                double angle = Math.atan2(other.getY() - y, other.getX() - x);

                other.takeDamage(contactDamage, contactKnockback, angle, contactStun, contactStunFriction);
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
    public void drawShadow(ShadowRenderer renderer, int batchIndex) {
        float width = sprite[batchIndex].getRegionWidth();
        float height = sprite[batchIndex].getRegionHeight();
        renderer.drawShadow((float)x - width/2, (float)y + height/2, width, -height, sprite[batchIndex]);
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
