package com.fireball.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fireball.game.rendering.fire.FireRenderer;
import com.fireball.game.rendering.textures.TextureData;
import com.fireball.game.rendering.textures.TextureManager;
import com.fireball.game.rooms.rooms.RoomCamera;
import com.fireball.game.entities.hitboxes.BodyHitbox;
import com.fireball.game.input.ControlMapping;
import com.fireball.game.input.InputManager;
import com.fireball.game.util.Util;

import java.util.LinkedList;

import static java.lang.Math.*;

public class Player extends Entity {
    private BodyHitbox hitbox;

    private final double maxHealth = 1;
    private double health = maxHealth;
    private double radius = 12;
    private double accel = 1000;
    private double friction = 0.75;
    private double maxSpeed = 150;
    private double turnAssist = 2.0;

    private double maxPushSpeed = 200;
    private double pushVelX = 0;
    private double pushVelY = 0;


    private RoomCamera roomCamera;
    private int currentWeaponIndex = 0;
    private boolean firingWeapon = false, prevFiringWeapon = false;
    private double targetX = 0, targetY = 0;

    public Player(int x, int y) {
        super(Team.PLAYER);
        this.x = x;
        this.y = y;
        terrainCollisionRadius = radius;

        //squareTexture = TextureManager.getTextureRegion(TextureData.SHAPES, 7);
        //triangleTexture = TextureManager.getTextureRegion(TextureData.SHAPES, 16 + 4);
        //circleTexture = TextureManager.getTextureRegion(TextureData.SHAPES, 32 + 0);

        hitbox = new BodyHitbox(this, team, radius) {
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
        hitbox.setPosition(x, y);

        bodyHitboxes = new BodyHitbox[] {hitbox};
        registerEntityAndHitboxes();
    }

    @Override
    public void updatePre(double delta) {
        LinkedList<Double[]> heldKeys = InputManager.getHeldKeys();

        //check input
        int moveX = 0;
        int moveY = 0;
        prevFiringWeapon = firingWeapon;
        firingWeapon = false;
        for(Double[] keys: heldKeys) {
            //System.out.println(keys[0] + " " + keys[1]);
            if(keys[0] == ControlMapping.MOVE_LEFT)
                moveX--;
            if(keys[0] == ControlMapping.MOVE_RIGHT)
                moveX++;
            if(keys[0] == ControlMapping.MOVE_UP)
                moveY--;
            if(keys[0] == ControlMapping.MOVE_DOWN)
                moveY++;

            if(keys[0] == ControlMapping.FIRE_WEAPON)
                firingWeapon = true;
        }


        //accelerate
        if(moveX != 0) {
            xVel += moveX * accel * delta;
        }
        if(moveY != 0) {
            yVel += moveY * accel * delta;
        }


        //cap max speed
        double vel = Math.hypot(xVel, yVel);
        double angle = Math.atan2(yVel, xVel);
        if(vel > maxSpeed) {
            vel = signum(vel) * min(maxSpeed, abs(vel) - accel * delta);
        } else if(moveX == 0 && moveY == 0) {
            vel = signum(vel) * max(0, abs(vel) - accel * friction * delta);
        }

        //assist in turning
        if(moveX != 0 || moveY != 0) {
            double targetAngle = Math.atan2(moveY, moveX);
            double turnAssistAmount = Math.cos(targetAngle) * Math.cos(angle) + Math.sin(targetAngle) * Math.sin(angle);
            //System.out.println(turnAssistAmount);

            double angleDiff = targetAngle - angle;
            if(angleDiff > Math.PI)
                angleDiff -= 2*Math.PI;
            if(angleDiff < -Math.PI)
                angleDiff += 2*Math.PI;

            angle += angleDiff * Math.min(1, turnAssist * turnAssistAmount * delta);
        }
        xVel = vel * cos(angle);
        yVel = vel * sin(angle);


        //set next position for collision detection
        nextX = x + (xVel + pushVelX) * delta;
        nextY = y + (yVel + pushVelY) * delta;

        pushVelX = 0;
        pushVelY = 0;
    }

    @Override
    public void updatePost(double delta) {
        x = nextX;
        y = nextY;

        hitbox.setPosition(x, y);

        targetX = InputManager.getMouseX() - roomCamera.viewportWidth/2 + roomCamera.position.x - x;
        targetY = InputManager.getMouseY() - roomCamera.viewportHeight/2 + roomCamera.position.y - y;
    }

    @Override
    public void draw(SpriteBatch batch) {
        /*float fireRadius = (float)radius * 2;
        batch.draw(fireTexture,
                (float)(x-fireRadius),
                (float)(y-fireRadius),
                fireRadius*2,
                fireRadius*2);*/

        //sprite.draw(batch);
    }

    @Override
    public void drawFire(FireRenderer renderer) {
        renderer.drawFire((float)x, (float)y, (float)radius*1, 1.0f);
    }

    @Override
    public void drawLight(FireRenderer renderer) {
        renderer.drawLight((float)x, (float)y, (float)radius*2, 1.0f);
    }

    public void setRoomCamera(RoomCamera roomCamera) {
        this.roomCamera = roomCamera;
    }

    @Override
    public void eventTerrainCollision(double angle) {

    }

    @Override
    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public void kill() {
        health = 0;
    }

    @Override
    public void updateSlotPositions(double slotSize) {
        slotMinX = (int)floor((x - radius) / slotSize);
        slotMaxX = (int)floor((x + radius) / slotSize);
        slotMinY = (int)floor((y - radius) / slotSize);
        slotMaxY = (int)floor((y + radius) / slotSize);
    }

    public double getTargetX() {
        return targetX;
    }

    public double getTargetY() {
        return targetY;
    }
}
