package com.fireball.game.entities.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.Entity;
import com.fireball.game.entities.Team;
import com.fireball.game.rendering.fire.FireRenderer;
import com.fireball.game.rendering.textures.TextureData;
import com.fireball.game.rendering.textures.TextureManager;
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

    private int[] abilityKeys;
    private RoomCamera roomCamera;

    public Player(int x, int y) {
        super(Team.PLAYER, "player", x, y, PlayerData.getCurrentAbilities(), PlayerData.getMaxCombo());

        abilityKeys = PlayerData.getAbilityKeys();

        DataFile.setCurrentLocation("entities", "player");
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
        registerEntityAndHitboxes();
    }

    @Override
    public void updatePre(double delta) {
        LinkedList<Double[]> heldKeys = InputManager.getHeldKeys();

        //check input
        moveX = 0;
        moveY = 0;
        for(int i = 0; i < abilities.length; i++) {
            prevAbilityInputs[i] = abilityInputs[i];
        }
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
        }
        for(int i = 0; i < abilityKeys.length; i++) {
            abilityInputs[i] = false;
            for(Double[] keys: heldKeys) {
                if(keys[0] == abilityKeys[i])
                    abilityInputs[i] = true;
            }
        }

        updateAbilities(delta);
        move(delta);

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

        targetX = InputManager.getMouseX() - roomCamera.viewportWidth/2 + roomCamera.getX() - x;
        targetY = InputManager.getMouseY() - roomCamera.viewportHeight/2 + roomCamera.getY() - y;
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
    public void updateSlotPositions(double slotSize) {
        slotMinX = (int)floor((x - radius) / slotSize);
        slotMaxX = (int)floor((x + radius) / slotSize);
        slotMinY = (int)floor((y - radius) / slotSize);
        slotMaxY = (int)floor((y + radius) / slotSize);
    }
}
