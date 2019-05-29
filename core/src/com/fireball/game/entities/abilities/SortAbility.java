package com.fireball.game.entities.abilities;

import java.util.Comparator;

public class SortAbility implements Comparator<Ability> {
    @Override
    public int compare(Ability a1, Ability a2) {
        return a1.getString().compareTo(a2.getString());
    }
}
