package com.fireball.game.entities.abilities;

public enum AbilityType {
    FIREBALL ("fireball", CooldownType.NORMAL),
    FLAMETHROWER ("flamethrower", CooldownType.NORMAL),
    RING ("ring", CooldownType.NORMAL),
    DASH ("dash", CooldownType.NORMAL);

    private String string;
    private CooldownType cooldownType;
    AbilityType(String string, CooldownType cooldownType) {
        this.string = string;
        this.cooldownType = cooldownType;
    }
    public String getString() {
        return string;
    }
    public CooldownType getCooldownType() {
        return cooldownType;
    }
}
