package com.fireball.game.entities.abilities;

public enum AbilityType {
    FIREBALL ("fireball"),
    FLAMETHROWER ("flamethrower"),
    RING ("ring"),
    DASH ("dash");

    private String string;
    AbilityType(String string) {
        this.string = string;
    }
    public String getString() {
        return string;
    }
}
