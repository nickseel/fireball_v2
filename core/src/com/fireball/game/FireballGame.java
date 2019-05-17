package com.fireball.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fireball.game.input.ControlMapping;
import com.fireball.game.input.InputManager;
import com.fireball.game.rooms.RoomData;
import com.fireball.game.textures.TextureManager;
import com.fireball.game.ui.FontManager;
import com.fireball.game.views.MainView;
import com.fireball.game.views.View;

public class FireballGame extends ApplicationAdapter {
	public static final String TITLE = "FIREBALL";
	public static final int WIDTH = 1600;
	public static final int HEIGHT = 900;

	private View mainView;
	private SpriteBatch batch;

	@Override
	public void create() {
		RoomData.processAllRooms();

		InputManager.init();
		TextureManager.loadTextures();
		FontManager.init();

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
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT |
				(Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

		batch.begin();
		mainView.draw(batch);
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		mainView.dispose();
		TextureManager.dispose();
		FontManager.dispose();
	}
}
