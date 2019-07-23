package com.fireball.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fireball.game.entities.player.PlayerData;
import com.fireball.game.input.ControlMapping;
import com.fireball.game.input.InputManager;
import com.fireball.game.rooms.rooms.Room;
import com.fireball.game.rooms.rooms.RoomData;
import com.fireball.game.rendering.textures.ColorTheme;
import com.fireball.game.rendering.textures.TextureData;
import com.fireball.game.rendering.textures.TextureManager;
import com.fireball.game.rendering.ui.FontManager;
import com.fireball.game.util.DataFile;
import com.fireball.game.views.MainView;
import com.fireball.game.views.View;

public class FireballGame extends ApplicationAdapter {
	public static final String TITLE = "FIREBALL";
	public static final int WIDTH = 2600;
	public static final int HEIGHT = 1700;

	private View mainView;
	private SpriteBatch batch;

	@Override
	public void create() {
		RoomData.processAllRooms();
		DataFile.loadJsonFile("abilities");
		DataFile.loadJsonFile("entities");

		InputManager.init();
		TextureManager.loadTextures();
		ColorTheme.init(TextureData.COLOR_THEME);
		FontManager.init();
		PlayerData.initDefault();

		Room.fromFile(null, RoomData.DEBUG);

		mainView = new MainView(WIDTH, HEIGHT);
		batch = new SpriteBatch();
		batch.enableBlending();
	}

	@Override
	public void render() {
		float delta = Gdx.graphics.getDeltaTime();

		mainView.update(delta);
		if(InputManager.keyPressed(ControlMapping.ESCAPE))
			Gdx.app.exit();
		InputManager.update(delta);

		mainView.preDraw();

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		batch.begin();
		mainView.draw(batch);
		batch.end();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void dispose() {
		batch.dispose();
		mainView.dispose();
		TextureManager.dispose();
		FontManager.dispose();
	}
}
