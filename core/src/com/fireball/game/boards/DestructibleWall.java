package com.fireball.game.boards;

public class DestructibleWall extends Wall {
    private boolean destroyed;

    public DestructibleWall(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2);
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
