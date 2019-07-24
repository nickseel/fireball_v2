package com.fireball.game.rendering.fire;

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

import java.awt.*;

public class FireRenderer {
    private static final float LIGHTING_SCENE_DARKNESS = 0.0f;
    private static final float LIGHTING_RESOLUTION_FACTOR = 0.5f;
    private static final float LIGHTING_SURFACE_SIZE_FACTOR = 1.0f;
    private static final int LIGHTING_NUM_REPETITIONS = 8;
    private static final int LIGHTING_INITIAL_REPETITION_SKIP = 1;
    private static final float LIGHTING_START_RADIUS = 6.0f;
    private static final float LIGHTING_REPETITION_RADIUS_INCREASE = 0.25f;
    private static final float LIGHTING_REPETITION_RADIUS_FACTOR = 1.25f;
    private static final float LIGHTING_CIRCLE_RESOLUTION = 12;

    private FrameBuffer rawFireBuffer, residualFireBuffer1, residualFireBuffer2, finalFireBuffer;
    private FrameBuffer lightBuffer1, lightBuffer2, lightOutputBuffer, lightResidualBuffer, finalLightBuffer;
    private int width, height;
    private float bufferScale, time, lastDelta;

    private FireResidualShader fireResidualShader;
    private FireFinalShader fireFinalShader;
    private LightInitialShader lightInitialShader;
    private LightResidualShader lightResidualShader;
    private LightFinalShader lightFinalShader;

    private RoomCamera camera, rawCamera;
    private SpriteBatch batch, defaultBatch;

    private Texture fireballTexture;

    public FireRenderer(int width, int height, float bufferScale) {
        this.width = width;
        this.height = height;
        this.bufferScale = bufferScale;
        rawFireBuffer = new FrameBuffer(Pixmap.Format.RGB888, (int)(width*LIGHTING_SURFACE_SIZE_FACTOR), (int)(height*LIGHTING_SURFACE_SIZE_FACTOR), false);
        residualFireBuffer1 = new FrameBuffer(Pixmap.Format.RGB888, (int)(width*LIGHTING_SURFACE_SIZE_FACTOR), (int)(height*LIGHTING_SURFACE_SIZE_FACTOR), false);
        residualFireBuffer2 = new FrameBuffer(Pixmap.Format.RGB888, (int)(width*LIGHTING_SURFACE_SIZE_FACTOR), (int)(height*LIGHTING_SURFACE_SIZE_FACTOR), false);
        finalFireBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)width, (int)height, false);
        lightBuffer1 = new FrameBuffer(Pixmap.Format.RGB888, (int)(width*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR), (int)(height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR), false);
        lightBuffer2 = new FrameBuffer(Pixmap.Format.RGB888, (int)(width*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR), (int)(height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR), false);
        lightOutputBuffer = new FrameBuffer(Pixmap.Format.RGB888, (int)(width*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR), (int)(height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR), false);
        lightResidualBuffer = new FrameBuffer(Pixmap.Format.RGB888, (int)(width*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR), (int)(height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR), false);
        finalLightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)width, (int)height, false);

        fireResidualShader = new FireResidualShader();
        fireFinalShader= new FireFinalShader();
        lightInitialShader = new LightInitialShader();
        lightResidualShader = new LightResidualShader();
        lightFinalShader = new LightFinalShader();

        batch = new SpriteBatch();
        defaultBatch = new SpriteBatch();

        rawCamera = new RoomCamera(width, height);

        fireballTexture = TextureManager.getTexture(TextureData.FIREBALL_BIG);
    }

    public void update(double delta) {
        lastDelta = (float)delta;
        time += delta;
    }

    public void begin(RoomCamera camera) {
        this.camera = camera;
        rawCamera.setPosition(camera.getX(), camera.getY());
        rawCamera.update(0);

        rawFireBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
        batch.setProjectionMatrix(rawCamera.combined);
        batch.begin();
        batch.setShader(null);
        //return batch;
    }

    public void drawFire(float centerX, float centerY, float fireRadius, float fireLevel) {
        drawFireTexture(fireballTexture, centerX, centerY, fireRadius, fireLevel);
    }

    public void drawFireTexture(Texture texture, float centerX, float centerY, float fireRadius, float fireLevel) {
        batch.setColor(fireLevel, 0, 0, 1);
        batch.draw(texture, centerX-fireRadius, centerY-fireRadius, fireRadius*2, fireRadius*2);
    }

    public void drawFireTexture(Texture texture, float centerX, float centerY, float width, float height, float rotation, float fireLevel) {
        batch.setColor(fireLevel, 0, 0, 1);
        batch.draw(new TextureRegion(texture), centerX-width/2, centerY-height/2, width/2, height/2, width, height, 1, 1, rotation);
    }

    public void end() {
        batch.setColor(Color.WHITE);
        batch.end();
        rawFireBuffer.end();

        //residual
        residualFireBuffer1.bind();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        defaultBatch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
        defaultBatch.begin();
        defaultBatch.setShader(fireResidualShader);
        fireResidualShader.loadUniforms(
                residualFireBuffer2.getColorBufferTexture(),
                camera,
                width*LIGHTING_SURFACE_SIZE_FACTOR, height*LIGHTING_SURFACE_SIZE_FACTOR,
                time);

        defaultBatch.draw(rawFireBuffer.getColorBufferTexture(),
                0, height*LIGHTING_SURFACE_SIZE_FACTOR, width*LIGHTING_SURFACE_SIZE_FACTOR, -height*LIGHTING_SURFACE_SIZE_FACTOR);

        defaultBatch.end();
        FrameBuffer.unbind();


        residualFireBuffer2.bind();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        defaultBatch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
        defaultBatch.begin();
        defaultBatch.setShader(null);

        defaultBatch.draw(residualFireBuffer1.getColorBufferTexture(),
                0, height*LIGHTING_SURFACE_SIZE_FACTOR, width*LIGHTING_SURFACE_SIZE_FACTOR, -height*LIGHTING_SURFACE_SIZE_FACTOR);

        defaultBatch.end();
        FrameBuffer.unbind();


        finalFireBuffer.bind();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        defaultBatch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ZERO);
        defaultBatch.begin();
        defaultBatch.setShader(fireFinalShader);
        fireFinalShader.loadUniforms(
                camera,
                width*LIGHTING_SURFACE_SIZE_FACTOR, height*LIGHTING_SURFACE_SIZE_FACTOR,
                time);

        defaultBatch.draw(residualFireBuffer2.getColorBufferTexture(),
                -(width*(LIGHTING_SURFACE_SIZE_FACTOR-1)/2), height+(height*(LIGHTING_SURFACE_SIZE_FACTOR-1)/2),
                width*LIGHTING_SURFACE_SIZE_FACTOR, -height*LIGHTING_SURFACE_SIZE_FACTOR);
        defaultBatch.end();
        FrameBuffer.unbind();



        ///////////////////////////////////////////////
        //                                           //
        //                 LIGHTING                  //
        //                                           //
        ///////////////////////////////////////////////

        lightBuffer2.bind();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        FrameBuffer.unbind();

        lightBuffer1.bind();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(residualFireBuffer2.getColorBufferTexture(), 0, height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR, width*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR, -height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR);
        batch.end();
        FrameBuffer.unbind();

        Gdx.gl.glColorMask(false, true, false, true);
        lightResidualBuffer.bind();
        batch.begin();
        batch.setShader(lightResidualShader);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        lightResidualShader.loadUniforms(lightBuffer1.getColorBufferTexture(), camera, width*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR, height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR, lastDelta);
        batch.draw(residualFireBuffer1.getColorBufferTexture(), 0, height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR, width*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR, -height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR);
        batch.end();
        FrameBuffer.unbind();

        lightBuffer1.bind();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        FrameBuffer.unbind();

        lightOutputBuffer.bind();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.setShader(lightInitialShader);
        lightInitialShader.setCutoff(0);
        batch.draw(lightResidualBuffer.getColorBufferTexture(), 0, height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR, width*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR, -height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR);
        batch.end();
        FrameBuffer.unbind();

        float currentRadius = LIGHTING_START_RADIUS;
        for(int i = 0; i < LIGHTING_NUM_REPETITIONS; i++) {
            lightBuffer1.bind();
            batch.enableBlending();
            batch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
            batch.begin();
            batch.setShader(lightInitialShader);
            lightInitialShader.setCutoff(1 - ((float)(i+1) / LIGHTING_NUM_REPETITIONS));
            batch.draw(lightResidualBuffer.getColorBufferTexture(), 0, height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR, width*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR, -height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR);
            batch.end();
            FrameBuffer.unbind();


            lightBuffer2.bind();
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.enableBlending();
            batch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);
            batch.begin();
            batch.setShader(null);
            for(float angle = 0; angle < 2*Math.PI; angle += (float)(2*Math.PI) / LIGHTING_CIRCLE_RESOLUTION) {
                batch.draw(lightBuffer1.getColorBufferTexture(),
                        (float)(currentRadius*Math.cos(angle))*LIGHTING_RESOLUTION_FACTOR,
                        (float)(currentRadius*Math.sin(angle))*LIGHTING_RESOLUTION_FACTOR + height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR,
                        width*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR, -height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR);
            }
            batch.end();
            FrameBuffer.unbind();


            if(i >= LIGHTING_INITIAL_REPETITION_SKIP) {
                lightOutputBuffer.bind();
                batch.begin();
                batch.setShader(null);
                batch.setColor(1, 1 / (float)(LIGHTING_NUM_REPETITIONS - LIGHTING_INITIAL_REPETITION_SKIP), 1, 1);
                batch.draw(lightBuffer2.getColorBufferTexture(), 0, height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR, width*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR, -height*LIGHTING_SURFACE_SIZE_FACTOR*LIGHTING_RESOLUTION_FACTOR);
                batch.setColor(Color.WHITE);
                batch.end();
                FrameBuffer.unbind();
            }


            FrameBuffer temp = lightBuffer2;
            lightBuffer2 = lightBuffer1;
            lightBuffer1 = temp;

            currentRadius *= LIGHTING_REPETITION_RADIUS_FACTOR;
            currentRadius += LIGHTING_REPETITION_RADIUS_INCREASE;
        }
        Gdx.gl.glColorMask(true, true, true, true);



        finalLightBuffer.bind();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setBlendFunction(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        batch.begin();
        batch.setShader(lightFinalShader);
        lightFinalShader.loadUniforms(width, height, time, LIGHTING_SCENE_DARKNESS);
        batch.draw(lightOutputBuffer.getColorBufferTexture(), 0, height, width, -height);
        batch.end();
        FrameBuffer.unbind();
    }

    public void drawFinalTextures(SpriteBatch batch, int width, int height) {
        batch.draw(finalFireBuffer.getColorBufferTexture(), 0, height, width, -height);
        batch.draw(finalLightBuffer.getColorBufferTexture(), 0, height, width, -height);
    }

    public void drawDebugTextures(SpriteBatch batch) {
        FrameBuffer[] debugBuffers = new FrameBuffer[] {
                rawFireBuffer,
                residualFireBuffer1,
                residualFireBuffer2,
                finalFireBuffer,
                lightBuffer1,
                lightBuffer2,
                lightOutputBuffer,
                lightResidualBuffer,
                finalLightBuffer
        };
        float w = (float)Gdx.graphics.getWidth() / (debugBuffers.length-1);
        float h = (float)Gdx.graphics.getHeight() / (debugBuffers.length-1);

        for(int i = 0; i < debugBuffers.length; i++) {
            batch.draw(debugBuffers[i].getColorBufferTexture(),
                    0, h*i, w-2, h-2);
        }
    }
}
