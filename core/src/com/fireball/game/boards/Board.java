package com.fireball.game.boards;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fireball.game.textures.TextureData;
import com.fireball.game.textures.TextureManager;
import com.fireball.game.views.GameView;

import java.util.ArrayList;

public abstract class Board {
    public static final double CELL_SIZE = 100.0;

    protected GameView parentView;

    protected CellSlotter<Wall> slottedStaticWalls;
    protected CellSlotter<DestructibleWall> slottedDynamicWalls;

    protected Wall[] staticWalls;
    protected ArrayList<DestructibleWall> dynamicWalls;
    private Sprite wallSprite;
    /*protected FloatBuffer vertexBuffer;
    protected IntBuffer indexBuffer;
    protected int vertexVbo, vao;*/

    public Board(GameView parentVew) {
        this.parentView = parentVew;
        slottedStaticWalls = new CellSlotter<Wall>();
        slottedDynamicWalls = new CellSlotter<DestructibleWall>();

        wallSprite = new Sprite(TextureManager.getTexture(TextureData.TEST_IMAGE));
    }

    protected void updateWallSlotPositions() {
        for(Wall wall: staticWalls) {
            wall.updateSlotPositions(Board.CELL_SIZE);
        }
        for(Wall wall: dynamicWalls) {
            wall.updateSlotPositions(Board.CELL_SIZE);
        }
    }

    protected void slotWalls() {
        slottedStaticWalls.clear();
        slottedStaticWalls.addAll(staticWalls);
        slottedDynamicWalls.clear();
        slottedDynamicWalls.addAll(dynamicWalls);
    }

    public void draw(SpriteBatch batch) {
        for(Wall w: staticWalls) {
            drawWall(w, batch);
        }
        for(Wall w: dynamicWalls) {
            drawWall(w, batch);
        }
    }

    private void drawWall(Wall w, SpriteBatch batch) {
        wallSprite.setOrigin((float)w.getLength()/2, 5);
        wallSprite.setSize((float)w.getLength(), 10);
        wallSprite.setRotation((float)Math.toDegrees(w.getAngle()));
        wallSprite.setCenter((float)w.getCenterX(), (float)w.getCenterY());
        wallSprite.draw(batch);
    }

    public void update(double delta) {
        for(int i = 0; i < dynamicWalls.size(); i++) {
            if(dynamicWalls.get(i).isDestroyed())
                dynamicWalls.remove(i--);
        }
    }

    public Wall[] getStaticWalls() {
        return staticWalls;
    }

    public ArrayList<DestructibleWall> getDynamicWalls() {
        return dynamicWalls;
    }

    public CellSlotter<Wall> getSlottedStaticWalls() {
        return slottedStaticWalls;
    }

    public CellSlotter<DestructibleWall> getSlottedDynamicWalls() {
        return slottedDynamicWalls;
    }
}
