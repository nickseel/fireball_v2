package com.fireball.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.fireball.game.entities.enemies.Walker;
import com.fireball.game.rendering.fire.FireRenderer;
import com.fireball.game.rendering.shadow.ShadowRenderer;
import com.fireball.game.rooms.rooms.Room;
import com.fireball.game.rooms.rooms.RoomCamera;
import com.fireball.game.rooms.rooms.RoomData;
import com.fireball.game.entities.EntityManager;
import com.fireball.game.entities.player.Player;
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

    private float baseZoom;
    private int bufferWidth, bufferHeight;
    private FrameBuffer gameFrameBuffer;
    private SpriteBatch bufferBatch, defaultBatch;

    private FireRenderer fireRenderer;
    private ShadowRenderer shadowRenderer;
    private ColorThemeShader colorThemeShader;


    public GameView(View parentView, int width, int height, float baseZoom) {
        super(parentView, width, height);

        this.baseZoom = baseZoom;
        bufferWidth = (int)((width / baseZoom) + baseZoom*2);
        bufferHeight = (int)((height / baseZoom) + baseZoom*2);

        //pauseMenuView = new PauseMenuView(this, width, height);

        room = Room.fromFile(this, RoomData.DEBUG);
        entityManager = new EntityManager();
        entityManager.setRoom(room);
        camera = new RoomCamera(bufferWidth, bufferHeight);
        camera.setZoom(1);

        gameFrameBuffer = new FrameBuffer(Pixmap.Format.RGB888, bufferWidth, bufferHeight, false);
        gameFrameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        bufferBatch = new SpriteBatch();
        defaultBatch = new SpriteBatch();

        fireRenderer = new FireRenderer(bufferWidth, bufferHeight);
        shadowRenderer = new ShadowRenderer(bufferWidth, bufferHeight);
        colorThemeShader = new ColorThemeShader();

        Player p;
        if(room.getSpawnPoint() == null) {
            System.out.println("INITIAL ROOM IS MISSING SPAWN POINT!");
            p = new Player(0, 0);
        } else {
            p = new Player(room.getSpawnPoint().getX(), room.getSpawnPoint().getY());
        }
        p.setRoomCamera(camera);
        camera.follow(p, 16);

        new Walker((int)p.getX() + 100, (int)p.getY() + 1);
    }

    @Override
    public void update(double delta) {
        fireRenderer.update(delta);
        shadowRenderer.update(delta);

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
            entityManager.updateEntities(delta);
            camera.update(delta);
        }

        //pauseMenuView.update(delta);
    }

    @Override
    public void preDraw() {
        fireRenderer.begin(camera);
        entityManager.drawFire(fireRenderer);
        fireRenderer.end();

        shadowRenderer.begin(camera);
        entityManager.drawShadow(shadowRenderer);
        shadowRenderer.end(fireRenderer.getLightTexture());



        gameFrameBuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        bufferBatch.setProjectionMatrix(camera.combined);
        //bufferBatch.setTransformMatrix(camera.);

        bufferBatch.begin();
        bufferBatch.setShader(colorThemeShader);
        colorThemeShader.loadUniforms(ColorTheme.GROUND, true, room.getWallTiles().getWidth(), room.getWallTiles().getHeight());
        room.drawGround(bufferBatch);
        bufferBatch.end();

        bufferBatch.begin();
        bufferBatch.setShader(colorThemeShader);
        colorThemeShader.loadUniforms(ColorTheme.WALL, false, 0, 0);
        room.drawWalls(bufferBatch);
        bufferBatch.end();

        bufferBatch.begin();
        bufferBatch.setShader(null);
        entityManager.draw(bufferBatch);
        bufferBatch.end();

        defaultBatch.begin();
        fireRenderer.drawFinalTextures(defaultBatch, 0, height, width, -height);
        //shadowRenderer.drawFinalTextures(defaultBatch, 0, height, width, -height);
        defaultBatch.end();

        gameFrameBuffer.end();


        //pauseMenuView.preDraw();
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(gameFrameBuffer.getColorBufferTexture(),
                (width * 0.5f) - (bufferWidth * baseZoom * camera.getZoom() * 0.5f)
                        - ((camera.getX() * baseZoom) % baseZoom) + (0.5f * baseZoom),
                (height * 0.5f) - (bufferHeight * baseZoom * camera.getZoom() * 0.5f)
                        + ((camera.getY() * baseZoom) % baseZoom) - (0.5f * baseZoom),
                bufferWidth * baseZoom * camera.getZoom(),
                bufferHeight * baseZoom * camera.getZoom());

        fireRenderer.drawDebugTextures(batch, 9, 0);
        shadowRenderer.drawDebugTextures(batch, 9, 1);

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
        fireRenderer.dispose();
        shadowRenderer.dispose();
    }
}