package com.fireball.game.entities.hitboxes;

import com.fireball.game.rooms.collision.Slottable;
import com.fireball.game.entities.Entity;
import com.fireball.game.entities.Team;

import static java.lang.Math.floor;

public abstract class Hitbox extends Slottable {
    protected Entity owner;
    protected Team team;
    protected HitboxType type;

    protected double x, y;
    protected double radius;

    public Hitbox(Entity owner, Team team, double x, double y, double radius) {
        this.owner = owner;
        this.team = team;
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public boolean overlapping(Hitbox other) {
        return Math.sqrt((other.getX() - x)*(other.getX() - x) + (other.getY() - y)*(other.getY() - y)) < radius + other.getRadius();
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public Team getTeam() {
        return team;
    }

    public HitboxType getType() {
        return type;
    }

    public Entity getOwner() {
        return owner;
    }

    @Override
    public void updateSlotPositions(double slotSize) {
        slotMinX = (int)floor((x - radius) / slotSize);
        slotMaxX = (int)floor((x + radius) / slotSize);
        slotMinY = (int)floor((y - radius) / slotSize);
        slotMaxY = (int)floor((y + radius) / slotSize);
    }
}
