package com.fireball.game.rendering.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.fireball.game.rendering.shaders.*;
import com.fireball.game.rendering.textures.TextureData;
import com.fireball.game.rendering.textures.TextureManager;
import com.fireball.game.rooms.rooms.RoomCamera;

public class ShadowRenderer {
    private int width, height;
    private float time, lastDelta;
    private FrameBuffer shadowInitialBuffer, shadowDistortBuffer, finalShadowBuffer;

    private ShadowInitialShader shadowInitialShader;
    private ShadowDistortShader shadowDistortShader;
    private ShadowObstructionShader shadowObstructionShader;
    private ShadowFinalShader shadowFinalShader;

    private RoomCamera camera, tempCamera;
    private SpriteBatch mainBatch, obstructionBatch, colorBatch, defaultBatch;

    public ShadowRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        shadowInitialBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        shadowDistortBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        finalShadowBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);

        shadowInitialShader = new ShadowInitialShader();
        shadowDistortShader= new ShadowDistortShader();
        shadowObstructionShader = new ShadowObstructionShader();
        shadowFinalShader = new ShadowFinalShader();

        mainBatch = new SpriteBatch();
        obstructionBatch = new SpriteBatch();
        colorBatch = new SpriteBatch();
        defaultBatch = new SpriteBatch();

        tempCamera = new RoomCamera(width, height);
    }

    public void update(double delta) {
        lastDelta = (float)delta;
        time += delta;
    }

    public void begin(RoomCamera camera) {
        this.camera = camera;
        tempCamera.setPosition(camera.getX(), camera.getY());
        tempCamera.update(0);

        shadowInitialBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mainBatch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
        mainBatch.setProjectionMatrix(tempCamera.combined);
        mainBatch.begin();
        mainBatch.setColor(1f, 0f, 0f, 1f);
        mainBatch.setShader(shadowInitialShader);

        obstructionBatch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
        obstructionBatch.setProjectionMatrix(tempCamera.combined);
        obstructionBatch.begin();
        obstructionBatch.setColor(0f, 1f, 0f, 1f);
        obstructionBatch.setShader(shadowInitialShader);

        colorBatch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
        colorBatch.setProjectionMatrix(tempCamera.combined);
        colorBatch.begin();
        colorBatch.setColor(0f, 0f, 1f, 1f);
        colorBatch.setShader(shadowInitialShader);
    }

    public void drawShadow(float centerX, float centerY,
                           TextureRegion mainTexture, TextureRegion obstructionTexture, TextureRegion colorTexture) {
        mainBatch.draw(mainTexture, centerX, centerY);
        obstructionBatch.draw(obstructionTexture, centerX, centerY);
        colorBatch.draw(colorTexture, centerX, centerY);
    }

    public void end() {
        mainBatch.end();
        obstructionBatch.end();
        colorBatch.end();
        shadowInitialBuffer.end();
    }

    public void drawFinalTextures(SpriteBatch batch, float x, float y, float width, float height) {
        batch.draw(finalShadowBuffer.getColorBufferTexture(), x, y, width, height);
    }

    public void drawDebugTextures(SpriteBatch batch, int numMax, int xOffset) {
        FrameBuffer[] debugBuffers = new FrameBuffer[] {
                shadowInitialBuffer,
                shadowDistortBuffer,
                finalShadowBuffer
        };
        float w = (float) Gdx.graphics.getWidth() / numMax;
        float h = (float)Gdx.graphics.getHeight() / numMax;

        for(int i = 0; i < debugBuffers.length; i++) {
            batch.draw(debugBuffers[i].getColorBufferTexture(),
                    w*xOffset, h*i, w-2, h-2);
        }
    }

    public void dispose() {

    }
}
