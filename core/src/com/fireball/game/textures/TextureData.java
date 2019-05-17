package com.fireball.game.textures;

public enum TextureData {
    TEST_IMAGE ("badlogic.jpg"),
    SHAPES ("shapes.png", 8, 8),
    ROUNDED_RECT ("rounded_rect.png"),
    WEAPONS ("guns.png", 2, 2),
    DIAGONALS ("diagonals.png"),
    ARROW ("arrow.png");

    private String fileName;
    private int sheetRows, sheetCols;

    TextureData(String fileName) {
        this.fileName = fileName;
        sheetRows = 1;
        sheetCols = 1;
    }

    TextureData(String fileName, int sheetRows, int sheetCols) {
        this.fileName = fileName;
        this.sheetRows = sheetRows;
        this.sheetCols = sheetCols;
    }

    public String getFileName() {
        return fileName;
    }

    public void load() {
        TextureManager.loadTexture(this, sheetRows, sheetCols);
    }
}
