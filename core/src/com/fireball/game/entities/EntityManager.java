package com.fireball.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fireball.game.entities.hitboxes.Hitbox;
import com.fireball.game.rendering.fire.FireRenderer;
import com.fireball.game.rendering.shadow.ShadowRenderer;
import com.fireball.game.rooms.rooms.Room;
import com.fireball.game.rooms.collision.CellEventManager;
import com.fireball.game.rooms.collision.CellSlotter;
import com.fireball.game.rooms.collision.Wall;
import com.fireball.game.entities.hitboxes.BodyHitbox;
import com.fireball.game.entities.hitboxes.DamagerHitbox;

import java.util.HashMap;
import java.util.LinkedList;

public class EntityManager {
    public static EntityManager current = null;

    private static final double RECENT_HITBOX_COLLISION_TIMEOUT = 0.5;
    private static final double RECENT_HITBOX_COLLISION_DOT_TIMEOUT = 0.1;

    private LinkedList<Entity> newEntities;
    private LinkedList<Entity> playerEntities, enemyEntities;//, neutralEntities;

    private CellSlotter<Entity> slottedPlayerEntities, slottedEnemyEntities;
    private CellSlotter<BodyHitbox> slottedPlayerBodyHitboxes, slottedEnemyBodyHitboxes;
    private CellSlotter<DamagerHitbox> slottedPlayerDamagerHitboxes, slottedEnemyDamagerHitboxes;

    private CellSlotter<Entity> slottedIndividualEntity;
    private CellSlotter<BodyHitbox> slottedIndividualBodyHitbox;
    private CellSlotter<DamagerHitbox> slottedIndividualDamagerHitbox;

    private CellEventManager<Wall, Entity> staticTerrainCollisionEventManager;
    private CellEventManager<BodyHitbox, DamagerHitbox> hitboxCollisionEventManager;
    private CellEventManager<BodyHitbox, BodyHitbox> hitboxPushCollisionEventManager;

    private LinkedList<Double[]> recentHitboxCollisions;

    private Room room;

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

        slottedIndividualEntity = new CellSlotter<Entity>();
        slottedIndividualBodyHitbox = new CellSlotter<BodyHitbox>();
        slottedIndividualDamagerHitbox = new CellSlotter<DamagerHitbox>();


        staticTerrainCollisionEventManager = new CellEventManager<Wall, Entity>() {
            @Override
            public void event(Wall item1, Entity item2) {
                if(item2.terrainCollisionRadius != -1 && item1.collide(item2)) {
                    item2.eventTerrainCollision(item1.getAngle());
                }
            }
        };
        hitboxCollisionEventManager = new CellEventManager<BodyHitbox, DamagerHitbox>() {
            @Override
            public void event(BodyHitbox item1, DamagerHitbox item2) {
                if(item1.getOwner().isAlive() && item2.getOwner().isAlive() && item1.getTeam().collidesWidth(item2.getTeam())
                        && item2.overlapping(item1) && item1.isDamageable() && !isCollisionRecent(item1, item2)) {
                    item2.damageBody(item1);

                    if(item2.isDamageOverTime()) {
                        addHitboxCollision(item1, item2, RECENT_HITBOX_COLLISION_DOT_TIMEOUT);
                    } else {
                        addHitboxCollision(item1, item2, RECENT_HITBOX_COLLISION_TIMEOUT);
                    }
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

        recentHitboxCollisions = new LinkedList<Double[]>();
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void updateEntities(double delta) {
        setCurrent();

        updateRecentHitboxCollisions(delta);

        prepareEntityHitboxes();
        collideEntityBodies();

        for(Entity e: playerEntities) {
            e.updatePre(delta);
        }
        for(Entity e: enemyEntities) {
            e.updatePre(delta);
        }

        //EfficiencyMetrics.startTimer(EfficiencyMetricType.COLLISION);
        collideTerrain();
        //EfficiencyMetrics.stopTimer(EfficiencyMetricType.COLLISION);

        for(Entity e: playerEntities) {
            e.updateMid(delta);
        }
        for(Entity e: enemyEntities) {
            e.updateMid(delta);
        }

        collideEntities();

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
            slottedPlayerBodyHitboxes.addAndUpdateAll(e.getBodyHitboxes(), Room.CELL_SIZE);
            slottedPlayerDamagerHitboxes.addAndUpdateAll(e.getDamagerHitboxes(), Room.CELL_SIZE);
        }
        for(Entity e: enemyEntities) {
            slottedEnemyBodyHitboxes.addAndUpdateAll(e.getBodyHitboxes(), Room.CELL_SIZE);
            slottedEnemyDamagerHitboxes.addAndUpdateAll(e.getDamagerHitboxes(), Room.CELL_SIZE);
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

    private void collideTerrain() {
        slottedPlayerEntities.clear();
        slottedPlayerEntities.addAndUpdateAll(playerEntities, Room.CELL_SIZE);
        slottedEnemyEntities.clear();
        slottedEnemyEntities.addAndUpdateAll(enemyEntities, Room.CELL_SIZE);

        staticTerrainCollisionEventManager.callEvents(room.getSlottedStaticWalls(), slottedPlayerEntities);
        staticTerrainCollisionEventManager.callEvents(room.getSlottedStaticWalls(), slottedEnemyEntities);
    }

    public void collideIndividualEntity(Entity e) {
        slottedIndividualBodyHitbox.clear();
        slottedIndividualDamagerHitbox.clear();
        slottedIndividualBodyHitbox.addAndUpdateAll(e.getBodyHitboxes(), Room.CELL_SIZE);
        slottedIndividualDamagerHitbox.addAndUpdateAll(e.getDamagerHitboxes(), Room.CELL_SIZE);

        hitboxCollisionEventManager.callEvents(slottedIndividualBodyHitbox, slottedPlayerDamagerHitboxes);
        hitboxCollisionEventManager.callEvents(slottedIndividualBodyHitbox, slottedEnemyDamagerHitboxes);
        hitboxCollisionEventManager.callEvents(slottedPlayerBodyHitboxes, slottedIndividualDamagerHitbox);
        hitboxCollisionEventManager.callEvents(slottedEnemyBodyHitboxes, slottedIndividualDamagerHitbox);


        slottedIndividualEntity.clear();
        slottedIndividualEntity.addAndUpdate(e, Room.CELL_SIZE);
        staticTerrainCollisionEventManager.callEvents(room.getSlottedStaticWalls(), slottedIndividualEntity);
    }

    public void draw(SpriteBatch batch) {
        for(Entity e: playerEntities) {
            e.draw(batch);
        }
        for(Entity e: enemyEntities) {
            e.draw(batch);
        }
    }

    public void drawFire(FireRenderer renderer) {
        for(Entity e: playerEntities) {
            e.drawFire(renderer);
        }
        for(Entity e: enemyEntities) {
            e.drawFire(renderer);
        }
    }

    public void drawShadow(ShadowRenderer renderer) {
        for(int i = 0; i < 3; i++) {
            renderer.prepBatch(i);

            for(Entity e : playerEntities) {
                e.drawShadow(renderer, i);
            }
            for(Entity e : enemyEntities) {
                e.drawShadow(renderer, i);
            }
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

    private void addHitboxCollision(Hitbox h1, Hitbox h2, double time) {
        recentHitboxCollisions.add(new Double[] {(double)h1.getID(), (double)h2.getID(), time});
    }

    private void updateRecentHitboxCollisions(double delta) {
        for(int i = 0; i < recentHitboxCollisions.size(); i++) {
            Double[] collision = recentHitboxCollisions.get(i);
            collision[2] -= delta;

            if(collision[2] <= 0)
                recentHitboxCollisions.remove(i--);
        }
    }

    private boolean isCollisionRecent(Hitbox h1, Hitbox h2) {
        for(Double[] collision: recentHitboxCollisions) {
            if((h1.getID() == collision[0] && h2.getID() == collision[1]) ||
                    (h2.getID() == collision[0] && h1.getID() == collision[1]))
                return true;
        }
        return false;
    }

    public void setCurrent() {
        EntityManager.current = this;
    }

    public Entity nearestEntity(double x, double y, Team checkTeam, String checkName) {
        LinkedList<LinkedList<Entity>> validEntities = new LinkedList<LinkedList<Entity>>();
        if(checkTeam == null) {
            validEntities.add(playerEntities);
            validEntities.add(enemyEntities);
        } else if(checkTeam == Team.PLAYER) {
            validEntities.add(playerEntities);
        } else if(checkTeam == Team.ENEMY) {
            validEntities.add(enemyEntities);
        } else {
            return null;
        }


        double nearestDist = 0;
        Entity nearest = null;
        for(LinkedList<Entity> entities: validEntities) {
            for(Entity e: entities) {
                if(entityValid(e, checkTeam, checkName)) {
                    double dist = Math.hypot(e.getX() - x, e.getY() - y);
                    if(nearest == null || nearestDist > dist) {
                        nearest = e;
                        nearestDist = dist;
                    }
                }
            }
        }

        return nearest;
    }

    public LinkedList<Entity> findEntities(Team checkTeam, String checkName) {
        LinkedList<LinkedList<Entity>> validEntities = new LinkedList<LinkedList<Entity>>();
        if(checkTeam == null) {
            validEntities.add(playerEntities);
            validEntities.add(enemyEntities);
        } else if(checkTeam == Team.PLAYER) {
            validEntities.add(playerEntities);
        } else if(checkTeam == Team.ENEMY) {
            validEntities.add(enemyEntities);
        } else {
            return null;
        }

        LinkedList<Entity> foundEntities = new LinkedList<Entity>();
        for(LinkedList<Entity> entities: validEntities) {
            for(Entity e: entities) {
                if(entityValid(e, checkTeam, checkName)) {
                    foundEntities.add(e);
                }
            }
        }
        return foundEntities;
    }

    private boolean entityValid(Entity e, Team checkTeam, String checkName) {
        if(checkTeam != null && !checkTeam.equals(e.getTeam()))
            return false;

        if(checkName != null && !checkName.equals(e.getName()))
            return false;

        return true;
    }
}
