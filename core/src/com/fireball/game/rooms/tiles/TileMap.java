package com.fireball.game.rooms.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fireball.game.textures.TextureData;
import com.fireball.game.textures.TextureManager;

public class TileMap {
    private int[][] tiles;
    private TextureData texture;

    private int tileWidth, tileHeight;

    public TileMap(int[][] tiles, TextureData texture) {
        this.tiles = tiles;
        this.texture = texture;

        TextureRegion[] textureRegions = TextureManager.getTextureRegions(texture);
        tileWidth = textureRegions[0].getRegionWidth();
        tileHeight = textureRegions[0].getRegionHeight();
    }

    public void draw(SpriteBatch batch) {
        TextureRegion[] textureRegions = TextureManager.getTextureRegions(texture);

        for(int r = 0; r < tiles.length; r++) {
            for(int c = 0; c < tiles[0].length; c++) {
                if(tiles[r][c] >= 0)
                    batch.draw(textureRegions[tiles[r][c]], c * tileWidth, (r+1) * tileHeight, tileWidth, -tileHeight);
            }
        }
    }

    public int[][] getTiles() {
        return tiles;
    }

    public TextureData getTexture() {
        return texture;
    }

    public int getTilesX() {
        return tiles[0].length;
    }

    public int getTilesY() {
        return tiles.length;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getWidth() {
        return getTileWidth() * getTilesX();
    }

    public int getHeight() {
        return getTileHeight() * getTilesY();
    }
}
