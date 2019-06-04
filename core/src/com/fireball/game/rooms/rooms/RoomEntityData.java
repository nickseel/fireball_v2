package com.fireball.game.rooms.rooms;

public class RoomEntityData {
    private String name, type;
    private int x, y, width, height;

    public RoomEntityData(String name, String type, int x, int y, int width, int height) {
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

    public int getCenterX() {
        return x + width/2;
    }

    public int getCenterY() {
        return y + height/2;
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
