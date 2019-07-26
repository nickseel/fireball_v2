package com.fireball.game.rendering.textures;

public enum TextureSheetData {
    GROUND_SMALL (TextureData.GROUND_SMALL, 4, 4),
    GROUND_BIG (TextureData.GROUND_BIG, 4, 4),
    WALLS (TextureData.WALLS, 8, 8),
    WALLS_GRAY (TextureData.WALLS_GRAY, 8, 8),
    CRACK (TextureData.CRACK, 4, 4),
    WALKER (TextureData.WALKER, 2, 2);

    private TextureData textureData;
    private int numRows, numCols;
    TextureSheetData(TextureData texture, int numRows, int numCols) {
        this.textureData = texture;
        this.numRows = numRows;
        this.numCols = numCols;
    }

    public TextureData getTextureData() {
        return textureData;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public void load() {
        TextureManager.loadTextureSheet(this, numRows, numCols);
    }
}
