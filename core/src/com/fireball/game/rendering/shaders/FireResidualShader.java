package com.fireball.game.rendering.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.fireball.game.rooms.rooms.RoomCamera;

public class FireResidualShader extends Shader {
    private static final String NAME = "fireResidual";

    public FireResidualShader() {
        super(NAME);
    }

    public void loadUniforms(Texture prevTexture, RoomCamera camera, float width, float height, float time) {
        Gdx.gl.glActiveTexture(Gdx.gl.GL_TEXTURE1);
        prevTexture.bind();
        setUniformi("u_previous_frame", 1);
        setUniformf("u_camera_delta", camera.getDX() / width, camera.getDY() / height);
        setUniformf("u_camera_position", camera.getX() / width, camera.getY() / height);
        setUniformf("u_surface_size", width, height);
        setUniformf("u_time", time);
        setUniformf("u_random", (float)Math.random());
        setUniformf("u_freeze", 0);
    }
}
