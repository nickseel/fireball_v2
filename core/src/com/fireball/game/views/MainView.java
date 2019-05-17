package com.fireball.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fireball.game.ui.FontManager;

public class MainView extends View {
    private View gameView;

    public MainView(int width, int height) {
        super(null, width, height);
        gameView = new GameView(this, width, height);
    }

    @Override
    public void update(double delta) {
        updateSubViews(delta);
    }

    @Override
    public void preDraw() {
        preDrawSubViews();
    }

    @Override
    public void draw(SpriteBatch batch) {
        gameView.draw(batch);
        FontManager.aireExterior36.setColor(0, 0, 0, 1);
        FontManager.aireExterior36.draw(batch, "FPS " + Gdx.graphics.getFramesPerSecond(), 0, height);
    }

    @Override
    public void processViewAction(String action) {
        System.out.println("UNHANDLED VIEW ACTION: " + action);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
