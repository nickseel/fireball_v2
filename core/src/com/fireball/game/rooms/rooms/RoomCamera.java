package com.fireball.game.rooms.rooms;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.fireball.game.entities.Entity;

public class RoomCamera extends OrthographicCamera {
    private double followSpeed;
    private Entity following;
    private float x, y, prevX, prevY;

    private final float offsetX, offsetY;

    public RoomCamera(int viewWidth, int viewHeight, float baseZoom) {
        super(viewWidth, viewHeight);

        offsetX = -(viewWidth*(baseZoom-1))/2;
        offsetY = -(viewHeight*(baseZoom-1))/2;
        x = offsetX;
        y = offsetY;
        position.x = x;
        position.y = y;
    }

    public void update(double delta) {
        prevX = x;
        prevY = y;
        if(following != null) {
            double targetX = following.getX() + offsetX;
            double targetY = following.getY() + offsetY;
            x += (float)(Math.signum(targetX - x) * Math.min(1, delta * followSpeed) * Math.abs(targetX - x));
            y += (float)(Math.signum(targetY - y) * Math.min(1, delta * followSpeed) * Math.abs(targetY - y));
        }

        position.x = x;
        position.y = y;
        update();
    }

    public void follow(Entity entity, double followSpeed) {
        this.following = entity;
        this.followSpeed = followSpeed;
    }

    public float getPrevX() {
        return prevX;
    }

    public float getPrevY() {
        return prevY;
    }

    public float getDX() {
        return x - prevX;
    }

    public float getDY() {
        return y - prevY;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
