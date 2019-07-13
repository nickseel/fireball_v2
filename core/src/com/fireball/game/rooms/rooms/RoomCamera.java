package com.fireball.game.rooms.rooms;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.fireball.game.entities.Entity;

public class RoomCamera extends OrthographicCamera {
    private double followSpeed;
    private Entity following;
    private float x, y, prevX, prevY;
    private float baseZoom;

    private final float offsetX, offsetY;

    public RoomCamera(int viewWidth, int viewHeight, float baseZoom) {
        super(viewWidth, viewHeight);
        this.baseZoom = baseZoom;

        offsetX = 0;//-(viewWidth*((1/baseZoom)-1))/2;
        offsetY = 0;//-(viewHeight*((1/baseZoom)-1))/2;
        x = offsetX;
        y = offsetY;
        position.x = x;
        position.y = y;
        this.projection.setToOrtho2D(0, 0, viewWidth, viewHeight);
    }

    public void update(double delta) {
        prevX = x;
        prevY = y;
        if(following != null) {
            double targetX = following.getX();
            double targetY = following.getY();
            x += (float)(Math.signum(targetX - x) * Math.min(1, delta * followSpeed) * Math.abs(targetX - x));
            y += (float)(Math.signum(targetY - y) * Math.min(1, delta * followSpeed) * Math.abs(targetY - y));
        }

        position.x = (float)Math.floor(x + offsetX + 0.0f);
        position.y = (float)Math.floor(y + offsetY + 0.0f);
        update();
    }

    public void follow(Entity entity, double followSpeed) {
        this.following = entity;
        this.followSpeed = followSpeed;
    }

    public void setPosition(float x, float y) {
        this.x = x;// + offsetX;
        this.y = y;// + offsetY;
    }

    public void setPositionOffset(float x, float y) {
        this.x = x + offsetX;
        this.y = y + offsetY;
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

    public float getWidth() {
        return viewportWidth / baseZoom;
    }

    public float getHeight() {
        return viewportHeight / baseZoom;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getBaseZoom() {
        return baseZoom;
    }
}
