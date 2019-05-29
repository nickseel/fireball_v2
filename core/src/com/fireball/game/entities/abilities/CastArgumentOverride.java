package com.fireball.game.entities.abilities;

import com.fireball.game.entities.Entity;

public class CastArgumentOverride {
    public static final int ARGUMENT_OWNER = 0;
    public static final int ARGUMENT_CREATE_POSITION = 1;
    public static final int ARGUMENT_TARGET_POSITION = 2;
    public static final int ARGUMENT_ANGLE = 3;
    public static final int ARGUMENT_OTHER = 4;

    private int type;
    private Entity owner;
    private double createX, createY, targetX, targetY, angle;
    private double[] other;

    public CastArgumentOverride(int type) {
        this.type = type;
    }

    public CastArgumentOverride setOwner(Entity owner) {
        this.owner = owner;
        return this;
    }

    public CastArgumentOverride setCreatePosition(double createX, double createY) {
        this.createX = createX;
        this.createY = createY;
        return this;
    }

    public CastArgumentOverride setTargetPosition(double targetX, double targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
        return this;
    }

    public CastArgumentOverride setAngle(double angle) {
        this.angle = angle;
        return this;
    }

    public CastArgumentOverride setOther(double[] other) {
        this.other = other;
        return this;
    }

    public int getType() {
        return type;
    }

    public Entity getOwner() {
        return owner;
    }

    public double getCreateX() {
        return createX;
    }

    public double getCreateY() {
        return createY;
    }

    public double getTargetX() {
        return targetX;
    }

    public double getTargetY() {
        return targetY;
    }

    public double getAngle() {
        return angle;
    }

    public double[] getOther() {
        return other;
    }
}
