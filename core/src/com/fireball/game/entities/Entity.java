package com.fireball.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fireball.game.boards.Slottable;
import com.fireball.game.entities.hitboxes.BodyHitbox;
import com.fireball.game.entities.hitboxes.DamagerHitbox;

public abstract class Entity extends Slottable {
    protected Team team;
    protected double x, y;
    protected double xVel, yVel;
    protected double nextX, nextY;
    protected BodyHitbox[] bodyHitboxes = new BodyHitbox[0];
    protected DamagerHitbox[] damagerHitboxes = new DamagerHitbox[0];
    protected double terrainCollisionRadius = -1;

    public Entity(Team team) {
        this.team = team;
    }

    public Entity(Team team, double x, double y) {
        this.team = team;
        this.x = x;
        this.y = y;
    }

    protected void registerEntityAndHitboxes() {
        EntityManager.current.addEntity(this);
    }

    public abstract void updatePre(double delta);
    public abstract void updatePost(double delta);
    public abstract void draw(SpriteBatch batch);

    public abstract void eventTerrainCollision(double angle);

    public abstract boolean isAlive();
    public abstract void kill();

    public BodyHitbox[] getBodyHitboxes() {
        return bodyHitboxes;
    }

    public DamagerHitbox[] getDamagerHitboxes() {
        return damagerHitboxes;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getXVel() {
        return xVel;
    }

    public double getYVel() {
        return yVel;
    }

    public void setVelocity(double xVel, double yVel) {
        this.xVel = xVel;
        this.yVel = yVel;
    }

    public double getNextX() {
        return nextX;
    }

    public double getNextY() {
        return nextY;
    }

    public void setNextPosition(double nextX, double nextY) {
        this.nextX = nextX;
        this.nextY = nextY;
    }

    public double getTerrainCollisionRadius() {
        return terrainCollisionRadius;
    }

    public Team getTeam() {
        return team;
    }

    public String toString() {
        return team.toString() + " " + this.hashCode();
    }
}
