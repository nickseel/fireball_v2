package com.fireball.game.rendering.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.fireball.game.rendering.shaders.FireFinalShader;
import com.fireball.game.rendering.shaders.FireResidualShader;
import com.fireball.game.rendering.shaders.LightInitialShader;
import com.fireball.game.rendering.textures.TextureData;
import com.fireball.game.rendering.textures.TextureManager;
import com.fireball.game.rooms.rooms.RoomCamera;

public class FireRenderer {
    private static final float LIGHTING_SCENE_DARKNESS = 1.0f;
    private static final float LIGHTING_RESOLUTION_FACTOR = 0.5f;
    private static final float LIGHTING_SURFACE_SIZE_FACTOR = 1.5f;
    private static final int LIGHTING_NUM_REPETITIONS = 6;
    private static final int LIGHTING_INITIAL_REPETITION_SKIP = 1;
    private static final float LIGHTING_START_RADIUS = 6.0f;
    private static final float LIGHTING_REPETITION_RADIUS_INCREASE = 0.25f;
    private static final float LIGHTING_REPETITION_RADIUS_FACTOR = 1.25f;
    private static final float LIGHTING_CIRCLE_RESOLUTION = 12;

    private final float bufferScale;

    private FrameBuffer rawFireBuffer, residualFireBuffer1, residualFireBuffer2, finalFireBuffer;
    private FrameBuffer lightBuffer1, lightBuffer2, lightOutputBuffer, lightResidualBuffer, finalLightBuffer;
    private float width, height, time;

    private FireResidualShader fireResidualShader;
    private FireFinalShader fireFinalShader;
    private LightInitialShader lightInitialShader;

    private RoomCamera camera, rawCamera;
    private SpriteBatch batch;
    private Matrix4 defaultProjection;

    private Texture fireballTexture;

    public FireRenderer(int width, int height, float bufferScale) {
        this.width = width;
        this.height = height;
        this.bufferScale = bufferScale;
        rawFireBuffer = new FrameBuffer(Pixmap.Format.RGB888, (int)(width * LIGHTING_SURFACE_SIZE_FACTOR), (int)(height * LIGHTING_SURFACE_SIZE_FACTOR), false);
        residualFireBuffer1 = new FrameBuffer(Pixmap.Format.RGB888, (int)(width * LIGHTING_SURFACE_SIZE_FACTOR), (int)(height * LIGHTING_SURFACE_SIZE_FACTOR), false);
        residualFireBuffer2 = new FrameBuffer(Pixmap.Format.RGB888, (int)(width * LIGHTING_SURFACE_SIZE_FACTOR), (int)(height * LIGHTING_SURFACE_SIZE_FACTOR), false);
        finalFireBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        lightBuffer1 = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        lightBuffer2 = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        lightOutputBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        lightResidualBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        finalLightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);

        fireResidualShader = new FireResidualShader();
        fireFinalShader= new FireFinalShader();
        lightInitialShader = new LightInitialShader();

        batch = new SpriteBatch();
        defaultProjection = batch.getProjectionMatrix().cpy();

        rawCamera = new RoomCamera((int)(width*bufferScale),
                (int)(height*bufferScale),
                bufferScale / LIGHTING_SURFACE_SIZE_FACTOR);

        fireballTexture = TextureManager.getTexture(TextureData.FIREBALL_BIG);
    }

    public void update(double delta) {
        time += delta;
    }

    public void begin(RoomCamera camera) {
        this.camera = camera;
        rawCamera.setPosition(camera.getX(), camera.getY());
        rawCamera.update(0);

        rawFireBuffer.bind();
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
        batch.setColor(1, 0, 0, fireLevel);
        batch.draw(texture, centerX-fireRadius, centerY-fireRadius, fireRadius*2, fireRadius*2);
    }

    public void drawLight(float centerX, float centerY, float lightRadius, float lightLevel) {
        drawLightTexture(fireballTexture, centerX, centerY, lightRadius, lightLevel);
    }

    public void drawLightTexture(Texture texture, float centerX, float centerY, float lightRadius, float lightLevel) {
        batch.setColor(0, 1, 0, lightLevel);
        batch.draw(texture, centerX-lightRadius, centerY-lightRadius, lightRadius*2, lightRadius*2);
    }

    public void end() {
        batch.setColor(Color.WHITE);
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
                width*LIGHTING_SURFACE_SIZE_FACTOR, height*LIGHTING_SURFACE_SIZE_FACTOR,
                time);

        batch.draw(rawFireBuffer.getColorBufferTexture(),
                0, height*LIGHTING_SURFACE_SIZE_FACTOR, width*LIGHTING_SURFACE_SIZE_FACTOR, -height*LIGHTING_SURFACE_SIZE_FACTOR);

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

        batch.draw(residualFireBuffer1.getColorBufferTexture(),
                0, height*LIGHTING_SURFACE_SIZE_FACTOR, width*LIGHTING_SURFACE_SIZE_FACTOR, -height*LIGHTING_SURFACE_SIZE_FACTOR);

        batch.end();
        FrameBuffer.unbind();


        finalFireBuffer.bind();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setBlendFunction(Gdx.gl.GL_ONE, Gdx.gl.GL_ZERO);
        batch.setProjectionMatrix(defaultProjection);
        batch.begin();
        batch.setShader(fireFinalShader);
        fireFinalShader.loadUniforms(
                camera,
                width, height,
                time);

        batch.draw(residualFireBuffer2.getColorBufferTexture(),
                -(width*(LIGHTING_SURFACE_SIZE_FACTOR-1)/2), height+(height*(LIGHTING_SURFACE_SIZE_FACTOR-1)/2),
                width*LIGHTING_SURFACE_SIZE_FACTOR, -height*LIGHTING_SURFACE_SIZE_FACTOR);
        batch.end();
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
        FrameBuffer.unbind();

        Gdx.gl.glColorMask(false, true, false, true);
        lightResidualBuffer.bind();
        batch.begin();
        batch.setShader(null);
        //light residual here
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(rawFireBuffer.getColorBufferTexture(), 0, height, width, -height);
        batch.end();
        FrameBuffer.unbind();

        lightOutputBuffer.bind();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.setShader(lightInitialShader);
        lightInitialShader.setCutoff(0);
        batch.draw(lightResidualBuffer.getColorBufferTexture(), 0, height, width, -height);
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
            batch.draw(lightResidualBuffer.getColorBufferTexture(), 0, height, width, -height);
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
                        (float)(currentRadius*Math.cos(angle)),//*LIGHTING_RESOLUTION_FACTOR),
                        (float)(currentRadius*Math.sin(angle))/*LIGHTING_RESOLUTION_FACTOR)*/ + height,
                        width, -height);
            }
            batch.end();
            FrameBuffer.unbind();


            if(i >= LIGHTING_INITIAL_REPETITION_SKIP) {
                lightOutputBuffer.bind();
                batch.begin();
                batch.setShader(null);
                batch.setColor(1, LIGHTING_SCENE_DARKNESS / (float)(LIGHTING_NUM_REPETITIONS - LIGHTING_INITIAL_REPETITION_SKIP), 1, 1);
                batch.draw(lightBuffer2.getColorBufferTexture(), 0, height, width, -height);
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
    }

    public void drawFinalTextures(SpriteBatch batch) {
        batch.draw(finalFireBuffer.getColorBufferTexture(), 0, height, width, -height);
    }

    public void drawDebugTextures(SpriteBatch batch) {
        int i = 0;
        float f = 4;
        batch.draw(rawFireBuffer.getColorBufferTexture(),
                0, height*i++/f, width/f-2, height/f-2);
        batch.draw(residualFireBuffer1.getColorBufferTexture(),
                0, height*i++/f, width/f-2, height/f-2);
        batch.draw(residualFireBuffer2.getColorBufferTexture(),
                0, height*i++/f, width/f-2, height/f-2);
        batch.draw(finalFireBuffer.getColorBufferTexture(),
                0, height*i++/f, width/f-2, height/f-2);
        batch.draw(lightBuffer1.getColorBufferTexture(),
                0, height*i++/f, width/f-2, height/f-2);
        batch.draw(lightBuffer2.getColorBufferTexture(),
                0, height*i++/f, width/f-2, height/f-2);
        batch.draw(lightOutputBuffer.getColorBufferTexture(),
                0, height*i++/f, width/f-2, height/f-2);
        batch.draw(lightResidualBuffer.getColorBufferTexture(),
                0, height*i++/f, width/f-2, height/f-2);
        batch.draw(finalLightBuffer.getColorBufferTexture(),
                0, height*i++/f, width/f-2, height/f-2);
    }
}
