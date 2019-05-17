package com.fireball.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontManager {
    public static BitmapFont aireExterior12;
    public static BitmapFont aireExterior18;
    public static BitmapFont aireExterior24;
    public static BitmapFont aireExterior36;
    public static BitmapFont aireExterior48;
    public static BitmapFont aireExterior64;

    public static void init() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("AireExterior.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        parameter.size = 12;
        aireExterior12 = generator.generateFont(parameter);
        //aireExterior12.getData().markupEnabled = true;

        parameter.size = 18;
        aireExterior18 = generator.generateFont(parameter);
        //aireExterior18.getData().markupEnabled = true;

        parameter.size = 24;
        aireExterior24 = generator.generateFont(parameter);
        //aireExterior24.getData().markupEnabled = true;

        parameter.size = 36;
        aireExterior36 = generator.generateFont(parameter);
        //aireExterior36.getData().markupEnabled = true;

        parameter.size = 48;
        aireExterior48 = generator.generateFont(parameter);
        //aireExterior48.getData().markupEnabled = true;

        parameter.size = 64;
        aireExterior64 = generator.generateFont(parameter);
        //aireExterior64.getData().markupEnabled = true;

        generator.dispose();
    }

    public static void dispose() {
        aireExterior12.dispose();
        aireExterior18.dispose();
        aireExterior24.dispose();
        aireExterior36.dispose();
        aireExterior48.dispose();
        aireExterior64.dispose();
    }
}
