package com.fireball.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.fireball.game.rendering.fire.FireRenderer;
import com.fireball.game.rendering.textures.TextureManager;
import com.fireball.game.rooms.rooms.Room;
import com.fireball.game.rooms.rooms.RoomCamera;
import com.fireball.game.rooms.rooms.RoomData;
import com.fireball.game.entities.EntityManager;
import com.fireball.game.entities.Player;
import com.fireball.game.input.ControlMapping;
import com.fireball.game.input.InputManager;
import com.fireball.game.rendering.shaders.ColorThemeShader;
import com.fireball.game.rendering.textures.ColorTheme;

public class GameView extends View {
    private PauseMenuView pauseMenuView;
    private boolean paused = false;

    private EntityManager entityManager;
    private Room room;
    private RoomCamera camera;

    private FrameBuffer gameFrameBuffer;
    private SpriteBatch bufferBatch;
    private Matrix4 defaultProjection;

    private FireRenderer fireRenderer;
    private ColorThemeShader colorThemeShader;

    private final static float BUFFER_SCALE = 0.2f;
    private int bufferWidth, bufferHeight;

    public GameView(View parentView, int width, int height) {
        super(parentView, width, height);

        bufferWidth = (int)(width * BUFFER_SCALE);
        bufferHeight = (int)(height * BUFFER_SCALE);

        //pauseMenuView = new PauseMenuView(this, width, height);

        entityManager = new EntityManager();
        room = Room.fromFile(this, RoomData.DEBUG);
        camera = new RoomCamera(width, height, BUFFER_SCALE);

        gameFrameBuffer = new FrameBuffer(Pixmap.Format.RGB888, bufferWidth, bufferHeight, false);
        gameFrameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        bufferBatch = new SpriteBatch();
        defaultProjection = bufferBatch.getProjectionMatrix().cpy();

        fireRenderer = new FireRenderer(bufferWidth, bufferHeight);
        colorThemeShader = new ColorThemeShader();

        //camera.zoom = 1f/4;
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
        fireRenderer.update(delta);

        if(InputManager.keyPressed(ControlMapping.PAUSE_GAME)) {
            if(paused) {
                paused = false;
                //pauseMenuView.setFocused(false);
            } else {
                paused = true;
                //pauseMenuView.setFocused(true);
                //pauseMenuView.processViewAction("reset pause menu");
            }
        }

        if(!paused) {
            room.update(delta);
            entityManager.updateEntities(delta, room);
            camera.update(delta);
        }

        //pauseMenuView.update(delta);
    }

    @Override
    public void preDraw() {
        fireRenderer.begin(camera);
        entityManager.drawFire(fireRenderer);
        fireRenderer.end();


        gameFrameBuffer.bind();
        Gdx.gl.glClearColor(0.75f, 0.8f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        bufferBatch.setProjectionMatrix(camera.combined);
        bufferBatch.begin();

        bufferBatch.setShader(colorThemeShader);
        colorThemeShader.setColorThemeType(ColorTheme.WALL);
        room.draw(bufferBatch);

        bufferBatch.setShader(null);
        entityManager.draw(bufferBatch);

        //bufferBatch.draw(TextureManager.getColorTexture(Color.GREEN), (bufferWidth*(BUFFER_SCALE-1))/2, (bufferHeight*(BUFFER_SCALE-1))/2, 100, 100);

        bufferBatch.setProjectionMatrix(defaultProjection);
        bufferBatch.draw(fireRenderer.getFireTexture(), 0, bufferHeight, bufferWidth, -bufferHeight);

        bufferBatch.end();
        FrameBuffer.unbind();


        //pauseMenuView.preDraw();
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(gameFrameBuffer.getColorBufferTexture(), 0, 0,/*width/2-bufferWidth/2, height/2-bufferHeight/2*/ width, height);

        fireRenderer.drawDebugTextures(batch);

        //pauseMenuView.draw(batch);
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