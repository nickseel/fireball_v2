package com.fireball.game.entities.abilities;

import java.util.Comparator;

public class SortAbility implements Comparator<AbilityType> {
    @Override
    public int compare(AbilityType a1, AbilityType a2) {
        return a1.getString().compareTo(a2.getString());
    }
}
