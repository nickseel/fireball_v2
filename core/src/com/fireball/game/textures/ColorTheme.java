package com.fireball.game.textures;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class ColorTheme {
    private static final int SQUARE_SIZE = 4;

    public static final int GROUND = 0;
    public static final int WALL = 1;
    public static final int WOOD = 2;
    private static final int NUM_ROWS = 3;

    private static final int[] ROW_WIDTHS = new int[] {
            6, //ground
            6, //wall
            4  //wood
    };

    private static Color[][] colors;

    public static void init(TextureData colorTheme) {
        Texture texture = TextureManager.getTexture(colorTheme);
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        Pixmap pixmap = texture.getTextureData().consumePixmap();


        colors = new Color[NUM_ROWS][];

        for(int i = 0; i < NUM_ROWS; i++) {
            Color[] rowColors = new Color[ROW_WIDTHS[i]];
            for(int j = 0; j < ROW_WIDTHS[i]; j++) {
                rowColors[j] = new Color(pixmap.getPixel(j * SQUARE_SIZE + SQUARE_SIZE/2, i * SQUARE_SIZE + SQUARE_SIZE/2));
            }
            colors[i] = rowColors;
        }
    }

    public static Color[][] getColors() {
        return colors;
    }

    public static Color[] getColors(int row) {
        return colors[row];
    }
}
