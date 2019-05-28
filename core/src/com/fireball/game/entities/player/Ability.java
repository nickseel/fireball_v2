package com.fireball.game.entities.player;

public enum Ability {
    FIREBALL ("fireball"),
    FLAMETHROWER ("flamethrower"),
    RING ("ring"),
    DASH ("dash");

    private String string;
    Ability(String string) {
        this.string = string;
    }
    public String getString() {
        return string;
    }

    public static Ability[] getAbilities() {
        return Ability.values().clone();
    }
}
