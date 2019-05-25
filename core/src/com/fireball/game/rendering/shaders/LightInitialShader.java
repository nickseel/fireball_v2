package com.fireball.game.rendering.shaders;

public class LightInitialShader extends Shader {
    private static final String NAME = "lightInitial";

    public LightInitialShader() {
        super(NAME);
    }

    public void setCutoff(float cutoff) {
        setUniformf("u_cutoff", cutoff);
    }
}
