package com.fireball.game.rendering.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.fireball.game.rendering.shaders.FireResidualShader;
import com.fireball.game.rooms.rooms.RoomCamera;

public class FireRenderer {
    private FrameBuffer rawFireBuffer, residualFireBuffer1, residualFireBuffer2;
    private float width, height, time;

    private FireResidualShader fireResidualShader;

    private RoomCamera camera;
    private SpriteBatch batch;
    private Matrix4 defaultProjection;

    public FireRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        rawFireBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        residualFireBuffer1 = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        residualFireBuffer2 = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);

        fireResidualShader = new FireResidualShader();

        batch = new SpriteBatch();
        defaultProjection = batch.getProjectionMatrix().cpy();
    }

    public void update(double delta) {
        time += delta;
    }

    public void begin(RoomCamera camera) {
        this.camera = camera;

        rawFireBuffer.bind();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setShader(null);
        //return batch;
    }

    public void draw(Texture texture, float centerX, float centerY, float size, Color color) {
        batch.setColor(color);
        batch.draw(texture, centerX-size/2, centerY-size/2, size, size);
    }

    public void end() {
        batch.end();
        FrameBuffer.unbind();


        //residual
        residualFireBuffer1.bind();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
        batch.setProjectionMatrix(defaultProjection);
        batch.begin();
        batch.setShader(fireResidualShader);
        fireResidualShader.loadUniforms(
                residualFireBuffer2.getColorBufferTexture(),
                camera,
                width, height,
                time);
        Gdx.gl.glActiveTexture(Gdx.gl.GL_TEXTURE0);

        batch.draw(rawFireBuffer.getColorBufferTexture(), 0, height, width, -height);

        //Gdx.gl.glBindTexture(1, 0);
        batch.end();
        FrameBuffer.unbind();



        residualFireBuffer2.bind();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
        batch.setProjectionMatrix(defaultProjection);
        batch.begin();
        batch.setShader(null);

        batch.draw(residualFireBuffer1.getColorBufferTexture(), 0, height, width, -height);

        batch.end();
        FrameBuffer.unbind();



        //FrameBuffer temp = residualFireBuffer1;
        //residualFireBuffer1 = residualFireBuffer2;
        //residualFireBuffer2 = temp;
    }

    public Texture getFireTexture() {
        return rawFireBuffer.getColorBufferTexture();
    }

    public void drawDebugTextures(SpriteBatch batch) {
        batch.draw(rawFireBuffer.getColorBufferTexture(),
                width*0f, height*0f, width*0.25f-2, height*0.25f-2);
        batch.draw(residualFireBuffer1.getColorBufferTexture(),
                width*0f, height*0.25f, width*0.25f-2, height*0.25f-2);
        batch.draw(residualFireBuffer2.getColorBufferTexture(),
                width*0f, height*0.5f, width*0.25f-2, height*0.25f-2);
    }
}
