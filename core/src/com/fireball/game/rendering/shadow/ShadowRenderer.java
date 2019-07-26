package com.fireball.game.rendering.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.fireball.game.rendering.fire.FireRenderer;
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

    private SpriteBatch currentBatch = null;

    public ShadowRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        shadowInitialBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        shadowDistortBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        finalShadowBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);

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
        mainBatch.setColor(1f, 0f, 0f, 1f);
        mainBatch.setShader(shadowInitialShader);

        obstructionBatch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
        obstructionBatch.setProjectionMatrix(tempCamera.combined);
        obstructionBatch.setColor(0f, 1f, 0f, 1f);
        obstructionBatch.setShader(shadowInitialShader);

        colorBatch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
        colorBatch.setProjectionMatrix(tempCamera.combined);
        colorBatch.setColor(0f, 0f, 1f, 1f);
        colorBatch.setShader(shadowInitialShader);
    }

    public void prepBatch(int index) {
        if(currentBatch != null)
            currentBatch.end();

        switch(index) {
            default:
            case 0:
                currentBatch = mainBatch; break;
            case 1:
                currentBatch = obstructionBatch; break;
            case 2:
                currentBatch = colorBatch; break;
        }
        currentBatch.begin();
    }

    public void drawShadow(float centerX, float centerY, float width, float height, TextureRegion texture) {
        currentBatch.draw(texture, centerX, centerY, width, height);
    }

    public void end(Texture lightTexture) {
        currentBatch.end();
        currentBatch = null;
        shadowInitialBuffer.end();


        shadowDistortBuffer.bind();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        defaultBatch.begin();
        defaultBatch.setShader(shadowDistortShader);
        shadowDistortShader.loadUniforms(lightTexture, camera, time, FireRenderer.LIGHTING_SCENE_DARKNESS);
        defaultBatch.draw(shadowInitialBuffer.getColorBufferTexture(), 0, height, width, -height);
        defaultBatch.setShader(null);
        defaultBatch.end();
        FrameBuffer.unbind();


        finalShadowBuffer.bind();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        defaultBatch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
        defaultBatch.begin();
        defaultBatch.setShader(shadowFinalShader);
        defaultBatch.draw(shadowDistortBuffer.getColorBufferTexture(), 0, height, width, -height);
        defaultBatch.setShader(null);
        defaultBatch.end();
        defaultBatch.setBlendFunction(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        FrameBuffer.unbind();
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
