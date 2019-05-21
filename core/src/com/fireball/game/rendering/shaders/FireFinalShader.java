package com.fireball.game.rendering.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.fireball.game.rooms.rooms.RoomCamera;

public class FireFinalShader extends Shader {
    private static final String NAME = "fireFinal";

    public FireFinalShader() {
        super(NAME);
    }

    public void loadUniforms(RoomCamera camera, float width, float height, float time) {
        setUniformf("u_camera_position", camera.getX() / width, camera.getY() / height);
        setUniformf("u_surface_size", width, height);
        setUniformf("u_time", time);
    }
}
