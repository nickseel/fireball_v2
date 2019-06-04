package com.fireball.game.rooms.rooms;

import com.fireball.game.rooms.tiles.TileMap;
import com.fireball.game.rooms.collision.Wall;
import com.fireball.game.rendering.textures.TextureData;
import com.fireball.game.views.GameView;

public class RoomPreset1 extends Room {
    public RoomPreset1(GameView parentView) {
        super(parentView, new TileMap(new int[0][0], TextureData.TEST_IMAGE), new TileMap(new int[0][0], TextureData.TEST_IMAGE), new Wall[0], new RoomEntityData[0]);

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
