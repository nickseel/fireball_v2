package com.fireball.game.rendering.shaders;

import com.badlogic.gdx.graphics.Color;
import com.fireball.game.rendering.textures.ColorTheme;

public class ColorThemeShader extends Shader {
    private static final String NAME = "colorTheme";

    public ColorThemeShader() {
        super(NAME);
    }

    public void loadUniforms(int colorType, boolean noiseEnabled, double roomWidthX, double roomWidthY) {
        Color[] colors = ColorTheme.getColors(colorType);
        float[] values = new float[colors.length * 3];

        int i = 0;
        for(Color c: colors) {
            values[i++] = c.r;
            values[i++] = c.g;
            values[i++] = c.b;
        }

        setUniform3fv("u_colors", values, 0, colors.length * 3);
        setUniformi("u_numColors", colors.length);

        if(noiseEnabled)
            setUniformi("u_noiseEnabled", 1);
        else
            setUniformi("u_noiseEnabled", 0);
        setUniformf("u_pixelSize", 1/(float)roomWidthX, 1/(float)roomWidthY);
    }

    public void setNoiseEnabled(boolean enabled) {

    }
}
