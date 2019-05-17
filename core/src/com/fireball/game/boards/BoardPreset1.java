package com.fireball.game.boards;

import com.fireball.game.views.GameView;

public class BoardPreset1 extends Board {
    public BoardPreset1(GameView parentView) {
        super(parentView);

        walls = new Wall[] {
            new Wall(0, 350, 500, 250),
            new Wall(500, 250, 400, -350),
            new Wall(400, -350, 100, -150),
            new Wall(100, -150, -100, -400),
            new Wall(-100, -400, -500, 0),
            new Wall(-500, 0, 0, 350),
        };

        slotWalls();
    }
}
