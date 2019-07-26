package com.fireball.game.entities.player;

import com.badlogic.gdx.Gdx;
import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.Team;
import com.fireball.game.entities.enemies.ai.PlayerController;
import com.fireball.game.rendering.fire.FireRenderer;
import com.fireball.game.rooms.rooms.RoomCamera;
import com.fireball.game.entities.hitboxes.BodyHitbox;
import com.fireball.game.input.ControlMapping;
import com.fireball.game.input.InputManager;
import com.fireball.game.util.DataFile;
import com.fireball.game.util.Util;

import java.util.LinkedList;

import static java.lang.Math.*;

public class Player extends ControllableEntity {
    private BodyHitbox hitbox;

    private double healthRegen;
    private double radius = 12;

    private double maxPushSpeed = 200;
    private double pushVelX = 0;
    private double pushVelY = 0;

    private RoomCamera roomCamera;

    public Player(int x, int y) {
        super(Team.PLAYER, "player", x, y, PlayerData.getCurrentAbilities(), PlayerData.getMaxCombo(), new PlayerController());

        DataFile.setCurrentLocation("entities", "player");
        this.maxHealth = DataFile.getFloat("maxHealth"); this.health = maxHealth;
        this.healthRegen = DataFile.getFloat("healthRegen");
        this.radius = DataFile.getFloat("radius");
        //this.accel = DataFile.getFloat("accel");
        this.friction = DataFile.getFloat("friction");
        this.maxSpeed = DataFile.getFloat("maxSpeed");
        this.turnAssist = DataFile.getFloat("turnAssist");
        this.weight = DataFile.getFloat("weight");

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
        registerEntityAndHitboxes();
    }

    //////////////////////////////////////////////////////
    //               ENTITY UPDATE ORDER:               //
    //   1. Entities Push Each Other                    //
    //   2. [Update Pre]                                //
    //   3. Entities Collide With Terrain               //
    //   4. [Update Mid]                                //
    //   5. Entity Hitboxes Collide With Each Other     //
    //   6. [Update Post]                               //
    //   7. Dead Entities Are Kill()ed                  //
    //   8. Entities Created During Updates Are Added   //
    //////////////////////////////////////////////////////

    @Override
    public void updatePre(double delta) {
        //check input
        moveX = 0;
        moveY = 0;

        ai.run(this, delta);


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

        targetX = InputManager.getMouseX() - Gdx.graphics.getWidth()/2f + roomCamera.getX();
        targetY = InputManager.getMouseY() - Gdx.graphics.getHeight()/2f + roomCamera.getY();
    }

    @Override
    public void updatePost(double delta) {

    }

    @Override
    public void drawFire(FireRenderer renderer) {
        renderer.drawFire((float)x, (float)y, (float)radius*1, 1.0f);
    }

    public void setRoomCamera(RoomCamera roomCamera) {
        this.roomCamera = roomCamera;
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
