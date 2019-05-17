package com.fireball.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fireball.game.boards.Board;
import com.fireball.game.boards.CellEventManager;
import com.fireball.game.boards.CellSlotter;
import com.fireball.game.boards.Wall;
import com.fireball.game.entities.hitboxes.BodyHitbox;
import com.fireball.game.entities.hitboxes.DamagerHitbox;

import java.util.LinkedList;

public class EntityManager {
    public static EntityManager current = null;

    //private EntityRenderer entityRenderer;

    private LinkedList<Entity> newEntities;

    private LinkedList<Entity> playerEntities, enemyEntities;//, neutralEntities;

    private CellSlotter<Entity> slottedPlayerEntities, slottedEnemyEntities;
    private CellSlotter<BodyHitbox> slottedPlayerBodyHitboxes, slottedEnemyBodyHitboxes;
    private CellSlotter<DamagerHitbox> slottedPlayerDamagerHitboxes, slottedEnemyDamagerHitboxes;

    private CellEventManager<Wall, Entity> terrainCollisionEventManager;
    private CellEventManager<BodyHitbox, DamagerHitbox> hitboxCollisionEventManager;
    private CellEventManager<BodyHitbox, BodyHitbox> hitboxPushCollisionEventManager;

    public EntityManager() {
        setCurrent();

        //entityRenderer = new EntityRenderer();

        newEntities = new LinkedList<Entity>();

        playerEntities = new LinkedList<Entity>();
        enemyEntities = new LinkedList<Entity>();

        slottedPlayerEntities = new CellSlotter<Entity>();
        slottedEnemyEntities = new CellSlotter<Entity>();
        //neutralEntities = new LinkedList<>();

        slottedPlayerBodyHitboxes = new CellSlotter<BodyHitbox>();
        slottedEnemyBodyHitboxes = new CellSlotter<BodyHitbox>();
        slottedPlayerDamagerHitboxes = new CellSlotter<DamagerHitbox>();
        slottedEnemyDamagerHitboxes = new CellSlotter<DamagerHitbox>();


        terrainCollisionEventManager = new CellEventManager<Wall, Entity>() {
            @Override
            public void event(Wall item1, Entity item2) {
                if(item1.collide(item2)) {
                    item2.eventTerrainCollision(0);
                }
            }
        };
        hitboxCollisionEventManager = new CellEventManager<BodyHitbox, DamagerHitbox>() {
            @Override
            public void event(BodyHitbox item1, DamagerHitbox item2) {
                if(item1.getOwner().isAlive() && item2.getOwner().isAlive() && item2.overlapping(item1) && item1.isDamageable()) {
                    item2.damage(item1);
                }
            }
        };
        hitboxPushCollisionEventManager = new CellEventManager<BodyHitbox, BodyHitbox>() {
            @Override
            public void event(BodyHitbox item1, BodyHitbox item2) {
                if(!item1.equals(item2) && item1.getOwner().isAlive() && item2.getOwner().isAlive() && item2.overlapping(item1) && item1.isPushable() && item2.isPushable()) {
                    item1.getPushedBy(item2);
                    item2.getPushedBy(item1);
                }
            }
        };
    }

    public void updateEntities(double delta, Board board) {
        setCurrent();

        prepareEntityHitboxes();
        collideEntityBodies();

        for(Entity e: playerEntities) {
            e.updatePre(delta);
        }
        for(Entity e: enemyEntities) {
            e.updatePre(delta);
        }

        //EfficiencyMetrics.startTimer(EfficiencyMetricType.COLLISION);
        collideEntities();
        collideTerrain(board);
        //EfficiencyMetrics.stopTimer(EfficiencyMetricType.COLLISION);

        for(int i = 0; i < playerEntities.size(); i++) {
            Entity e = playerEntities.get(i);
            e.updatePost(delta);
            if(!e.isAlive()) {
                playerEntities.remove(i--).kill();
            }
        }
        for(int i = 0; i < enemyEntities.size(); i++) {
            Entity e = enemyEntities.get(i);
            e.updatePost(delta);
            if(!e.isAlive()) {
                enemyEntities.remove(i--).kill();
            }
        }

        addNewEntities();
    }

    private void prepareEntityHitboxes() {
        slottedPlayerBodyHitboxes.clear();
        slottedEnemyBodyHitboxes.clear();
        slottedPlayerDamagerHitboxes.clear();
        slottedEnemyDamagerHitboxes.clear();

        for(Entity e: playerEntities) {
            slottedPlayerBodyHitboxes.addAndUpdateAll(e.getBodyHitboxes(), Board.CELL_SIZE);
            slottedPlayerDamagerHitboxes.addAndUpdateAll(e.getDamagerHitboxes(), Board.CELL_SIZE);
        }
        for(Entity e: enemyEntities) {
            slottedEnemyBodyHitboxes.addAndUpdateAll(e.getBodyHitboxes(), Board.CELL_SIZE);
            slottedEnemyDamagerHitboxes.addAndUpdateAll(e.getDamagerHitboxes(), Board.CELL_SIZE);
        }
    }

    private void collideEntityBodies() {
        hitboxPushCollisionEventManager.callEvents(slottedPlayerBodyHitboxes, slottedEnemyBodyHitboxes);
        hitboxPushCollisionEventManager.callEvents(slottedPlayerBodyHitboxes, slottedPlayerBodyHitboxes);
        hitboxPushCollisionEventManager.callEvents(slottedEnemyBodyHitboxes, slottedEnemyBodyHitboxes);
    }

    private void collideEntities() {
        hitboxCollisionEventManager.callEvents(slottedPlayerBodyHitboxes, slottedEnemyDamagerHitboxes);
        hitboxCollisionEventManager.callEvents(slottedEnemyBodyHitboxes, slottedPlayerDamagerHitboxes);
    }

    private void collideTerrain(Board board) {
        slottedPlayerEntities.clear();
        slottedPlayerEntities.addAndUpdateAll(playerEntities, Board.CELL_SIZE);
        slottedEnemyEntities.clear();
        slottedEnemyEntities.addAndUpdateAll(enemyEntities, Board.CELL_SIZE);

        terrainCollisionEventManager.callEvents(board.getSlottedWalls(), slottedPlayerEntities);
        terrainCollisionEventManager.callEvents(board.getSlottedWalls(), slottedEnemyEntities);
    }

    public void draw(SpriteBatch batch) {
        for(Entity e: playerEntities) {
            e.draw(batch);
        }
        for(Entity e: enemyEntities) {
            e.draw(batch);
        }
    }

    public void addEntity(Entity e) {
        newEntities.add(e);
    }

    private void addNewEntities() {
        while(newEntities.size() > 0) {
            Entity e = newEntities.remove(0);

            switch (e.getTeam()) {
                case PLAYER:
                    playerEntities.add(e);
                    break;
                case ENEMY:
                    enemyEntities.add(e);
                    break;
            }
        }
    }

    public void setCurrent() {
        EntityManager.current = this;
    }
}
