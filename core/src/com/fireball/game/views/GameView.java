package com.fireball.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.fireball.game.rooms.rooms.Room;
import com.fireball.game.rooms.rooms.RoomCamera;
import com.fireball.game.rooms.rooms.RoomData;
import com.fireball.game.rooms.rooms.RoomPreset1;
import com.fireball.game.entities.EntityManager;
import com.fireball.game.entities.Player;
import com.fireball.game.input.ControlMapping;
import com.fireball.game.input.InputManager;

public class GameView extends View {
    private PauseMenuView pauseMenuView;
    private boolean paused = false;

    private EntityManager entityManager;
    private Room room;
    private RoomCamera camera;

    private FrameBuffer gameFrameBuffer;
    private SpriteBatch bufferBatch;

    public GameView(View parentView, int width, int height) {
        super(parentView, width, height);

        pauseMenuView = new PauseMenuView(this, width, height);

        entityManager = new EntityManager();
        room = Room.fromFile(this, RoomData.DEBUG);
        camera = new RoomCamera(width, height);

        gameFrameBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        gameFrameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        bufferBatch = new SpriteBatch();

        camera.zoom = 1f/4;
        Player p;
        if(room.getSpawnPoint() == null) {
            System.out.println("INITIAL ROOM IS MISSING SPAWN POINT!");
            p = new Player(0, 0);
        } else {
            p = new Player(room.getSpawnPoint().getX(), room.getSpawnPoint().getY());
        }
        p.setRoomCamera(camera);
        camera.follow(p, 16);
    }

    @Override
    public void update(double delta) {
        if(InputManager.keyPressed(ControlMapping.PAUSE_GAME)) {
            if(paused) {
                paused = false;
                pauseMenuView.setFocused(false);
            } else {
                paused = true;
                pauseMenuView.setFocused(true);
                pauseMenuView.processViewAction("reset pause menu");
            }
        }

        if(!paused) {
            room.update(delta);
            entityManager.updateEntities(delta, room);
            camera.update(delta);
        }

        pauseMenuView.update(delta);
    }

    @Override
    public void preDraw() {
        gameFrameBuffer.bind();
        Gdx.gl.glClearColor(0.75f, 0.8f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        bufferBatch.setProjectionMatrix(camera.combined);
        bufferBatch.begin();
        room.draw(bufferBatch);
        entityManager.draw(bufferBatch);
        bufferBatch.end();

        FrameBuffer.unbind();

        pauseMenuView.preDraw();
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(gameFrameBuffer.getColorBufferTexture(), 0, 0);

        pauseMenuView.draw(batch);
    }

    @Override
    public void processViewAction(String action) {
        sendViewAction(action); //pass action up the chain
    }

    @Override
    public void dispose() {
        super.dispose();
        gameFrameBuffer.dispose();
        bufferBatch.dispose();
    }
}