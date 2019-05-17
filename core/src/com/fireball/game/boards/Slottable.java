package com.fireball.game.boards;

public abstract class Slottable {
    protected int slotMinX, slotMaxX, slotMinY, slotMaxY;

    public abstract void updateSlotPositions(double slotSize);

    public int getSlotMinX() {
        return slotMinX;
    }

    public int getSlotMaxX() {
        return slotMaxX;
    }

    public int getSlotMinY() {
        return slotMinY;
    }

    public int getSlotMaxY() {
        return slotMaxY;
    }
}
