package com.fireball.game.boards;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.fireball.game.entities.Entity;

public class BoardCamera extends OrthographicCamera {
    private double followSpeed;
    private Entity following;

    public BoardCamera(int viewWidth, int viewHeight) {
        super(viewWidth, viewHeight);
    }

    public void update(double delta) {
        if(following != null) {
            double targetX = following.getX();
            double targetY = following.getY();
            position.set(
                    position.x + (float)(Math.signum(targetX - position.x) * Math.min(1, delta * followSpeed) * Math.abs(targetX - position.x)),
                    position.y + (float)(Math.signum(targetY - position.y) * Math.min(1, delta * followSpeed) * Math.abs(targetY - position.y)), 0);
        }

        update();
    }

    public void follow(Entity entity, double followSpeed) {
        this.following = entity;
        this.followSpeed = followSpeed;
    }
}
