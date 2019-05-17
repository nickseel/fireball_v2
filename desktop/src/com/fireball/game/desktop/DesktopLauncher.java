package com.fireball.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fireball.game.FireballGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = FireballGame.TITLE;
		config.width = FireballGame.WIDTH;
		config.height = FireballGame.HEIGHT;

		new LwjglApplication(new FireballGame(), config);
	}
}
