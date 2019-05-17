package com.fireball.game.rooms;

import com.fireball.game.rooms.walls.Wall;
import com.fireball.game.views.GameView;

public class RoomPreset1 extends Room {
    public RoomPreset1(GameView parentView) {
        super(parentView);

        staticWalls = new Wall[] {
            new Wall(0, 350, 500, 250),
            new Wall(500, 250, 400, -350),
            new Wall(400, -350, 100, -150),
            new Wall(100, -150, -100, -400),
            new Wall(-100, -400, -500, 0),
            new Wall(-500, 0, 0, 350),
        };

        updateWallSlotPositions();
        slotWalls();
    }
}
