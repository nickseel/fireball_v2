package com.fireball.game.rendering.textures;

public enum TextureData {
    TEST_IMAGE ("sprites/other/badlogic.jpg"),
    ROUNDED_RECT ("sprites/other/rounded_rect.png"),
    COLOR_THEME ("tilesets/color_theme.png"),
    FIREBALL ("sprites/player/fireball.png"),
    FIREBALL_BIG ("sprites/player/fireball_big.png"),
    LASER ("sprites/player/laser_main.png"),
    LASER_END ("sprites/player/laser_end.png"),
    GROUND_SMALL ("tilesets/ground_small.png"),
    GROUND_BIG ("tilesets/ground_big.png"),
    WALLS ("tilesets/walls.png"),
    WALLS_GRAY ("tilesets/walls_gray.png"),
    CRACK ("sprites/environment/crack.png"),
    WALKER ("sprites/enemies/walker.png");

    private String fileName;
    TextureData(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
    public void load() {
        TextureManager.loadTexture(this);
    }
}
