package com.fireball.game.rooms;

public class RoomJsonEntityData {
    private String name, type;
    private int x, y, width, height;

    public RoomJsonEntityData(String name, String type, int x, int y, int width, int height) {
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String toString() {
        return    "name:   " + name +
                "\ntype:   " + type +
                "\nx:      " + x +
                "\ny:      " + y +
                "\nwidth:  " + width +
                "\nheight: " + height + "\n";
    }
}
