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

    public static Ability[] getPlayerAbilities() {
        return new Ability[] {Ability.FIREBALL, Ability.FLAMETHROWER, Ability.RING, Ability.DASH};
    }
}
