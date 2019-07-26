package com.fireball.game.rendering.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.fireball.game.rooms.rooms.RoomCamera;

public class ShadowDistortShader extends Shader {
    private static final String NAME = "shadowDistort";

    public ShadowDistortShader() {
        super(NAME);
    }

    public void loadUniforms(Texture lightTexture, RoomCamera camera, float time, float max_darkness) {
        Gdx.gl.glActiveTexture(Gdx.gl.GL_TEXTURE1);
        lightTexture.bind();
        Gdx.gl.glActiveTexture(Gdx.gl.GL_TEXTURE0);

        setUniformi("u_light", 1);
        setUniformf("u_viewport", camera.getWidth(), camera.getHeight());
        setUniformf("u_camera_position", camera.getX(), camera.getY());
        setUniformf("u_time", time);
        setUniformf("u_max_darkness", max_darkness);
    }
}
