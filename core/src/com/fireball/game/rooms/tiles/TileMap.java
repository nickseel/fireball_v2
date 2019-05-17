package com.fireball.game.rooms.tiles;

import com.fireball.game.textures.TextureData;

public class TileMap {
    private int[][] tiles;
    private TextureData texture;

    public TileMap(int[][] tiles, TextureData texture) {
        this.tiles = tiles;
        this.texture = texture;
    }

    public int[][] getTiles() {
        return tiles;
    }

    public TextureData getTexture() {
        return texture;
    }

    public int getWidth() {
        return tiles[0].length;
    }

    public int getHeight() {
        return tiles.length;
    }
}
