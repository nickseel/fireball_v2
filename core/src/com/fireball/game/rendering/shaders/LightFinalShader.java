package com.fireball.game.rendering.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.fireball.game.rooms.rooms.RoomCamera;

public class LightFinalShader extends Shader {
    private static final String NAME = "lightFinal";

    public LightFinalShader() {
        super(NAME);
    }

    public void loadUniforms(float width, float height, float time, float maxDarkness) {
        setUniformf("u_scale", 1024/width, 768/height);
        setUniformf("u_time", time);
        //setUniformf("u_flicker", 0);
        setUniformf("u_max_darkness", maxDarkness);
    }
}
