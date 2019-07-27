package com.fireball.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fireball.game.entities.ControllableEntity;
import com.fireball.game.entities.Team;
import com.fireball.game.entities.abilities.AbilityType;
import com.fireball.game.entities.enemies.ai.DummyAI;
import com.fireball.game.entities.enemies.ai.WalkerAI;
import com.fireball.game.entities.hitboxes.BodyHitbox;
import com.fireball.game.entities.hitboxes.DamagerHitbox;
import com.fireball.game.input.InputManager;
import com.fireball.game.rendering.shadow.ShadowRenderer;
import com.fireball.game.rendering.textures.TextureData;
import com.fireball.game.rendering.textures.TextureManager;
import com.fireball.game.rendering.textures.TextureSheetData;
import com.fireball.game.util.DataFile;
import com.fireball.game.util.Util;

import java.util.LinkedList;

import static java.lang.Math.floor;

public class Dummy extends ControllableEntity {
    private Texture sprite;
    private BodyHitbox hitbox;

    private double radius;

    public Dummy(int x, int y) {
        super(Team.ENEMY, "walker", x, y, new AbilityType[0], 1);

        sprite = TextureManager.getTexture(TextureData.DUMMY);

        DataFile.setCurrentLocation("entities", "dummy");
        this.maxHealth = 1; this.health = maxHealth;
        this.radius = 10;
        this.weight = 1;

        ai = new DummyAI();

        this.terrainCollisionRadius = radius;

        hitbox = new BodyHitbox(this, team, x, y, radius) {
            @Override
            public void takeDamage(double damage, double knockback, double knockbackAngle, double stun, double stunFriction_) {
                xVel = (knockback * Math.cos(knockbackAngle)) / weight;
                yVel = (knockback * Math.sin(knockbackAngle)) / weight;
                stunTimer = Math.max(stunTimer, stun);
                stunFriction = stunFriction_;
            }

            @Override
            public void getPushedBy(BodyHitbox other) {
                //unpushable
            }
        };
        bodyHitboxes = new BodyHitbox[] {hitbox};

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

        ai.run(this, delta);

        //updateAbilities(delta);
        //move(delta);

        //set next position for collision detection
        nextX = x;// + (xVel + pushVelX) * delta;
        nextY = y;// + (yVel + pushVelY) * delta;
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
    public void draw(SpriteBatch batch) {
        float width = sprite.getWidth();
        float height = sprite.getHeight();
        batch.draw(sprite, (float)x - width/2, (float)y + height/2, width, -height);
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
