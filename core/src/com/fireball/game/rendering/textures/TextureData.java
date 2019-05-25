package com.fireball.game.rendering.textures;

public enum TextureData {
    TEST_IMAGE ("images/badlogic.jpg"),
    ROUNDED_RECT ("images/rounded_rect.png"),
    COLOR_THEME ("images/color_theme.png"),
    GROUND_SMALL ("images/ground_small.png", 4, 4),
    GROUND_BIG ("images/ground_big.png", 4, 4),
    WALLS ("images/walls.png", 8, 8),
    WALLS_GRAY ("images/walls_gray.png", 8, 8),
    FIREBALL ("images/fireball.png"),
    FIREBALL_BIG ("images/fireball_big.png");

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
