package com.fireball.game.rendering.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Shader extends ShaderProgram {
    private static final String FILE_PREFIX = "shaders/";

    public Shader(String name) {
        super(Gdx.files.internal(FILE_PREFIX + name + "Vertex.txt"),
                Gdx.files.internal(FILE_PREFIX + name + "Fragment.txt"));
        if(!isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + getLog());
    }
}
