package com.fireball.game.rendering.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.fireball.game.rooms.rooms.RoomCamera;

public class LightResidualShader extends Shader {
    private static final String NAME = "lightResidual";

    public LightResidualShader() {
        super(NAME);
    }

    public void loadUniforms(Texture prevTexture, RoomCamera camera, float width, float height, float delta) {
        Gdx.gl.glActiveTexture(Gdx.gl.GL_TEXTURE1);
        prevTexture.bind();
        Gdx.gl.glActiveTexture(Gdx.gl.GL_TEXTURE0);

        setUniformi("u_previous_frame", 1);
        setUniformf("u_camera_delta", camera.getDX() / width, camera.getDY() / height);
        setUniformf("u_delta_time", delta);
    }
}
