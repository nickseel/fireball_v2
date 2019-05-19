package com.fireball.game.rendering.shaders;

import com.badlogic.gdx.graphics.Color;
import com.fireball.game.rendering.textures.ColorTheme;

public class ColorThemeShader extends Shader {
    private static final String NAME = "colorTheme";

    public ColorThemeShader() {
        super(NAME);
    }

    public void setColorThemeType(int type) {
        Color[] colors = ColorTheme.getColors(type);
        float[] values = new float[colors.length * 3];

        int i = 0;
        for(Color c: colors) {
            values[i++] = c.r;
            values[i++] = c.g;
            values[i++] = c.b;
        }

        setUniform3fv("u_colors", values, 0, colors.length * 3);
        setUniformf("u_numColors", colors.length);
    }
}
