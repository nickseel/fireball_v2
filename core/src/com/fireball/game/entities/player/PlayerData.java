package com.fireball.game.entities.player;

import com.fireball.game.entities.abilities.AbilityType;

public class PlayerData {
    public static final int maxAbilities = 4;
    private static final int[] defaultKeys = new int[] {1000, 1001, 62, 59};

    private static int maxCombo;
    private static final AbilityType[] allAbilities = new AbilityType[] {AbilityType.FIREBALL, AbilityType.FLAMETHROWER, AbilityType.RING, AbilityType.DASH};
    private static boolean[] unlockedAbilities;
    private static int[] currentAbilities;
    private static int[] abilityKeys;

    public static void initDefault() {
        maxCombo = 2;

        unlockedAbilities = new boolean[allAbilities.length];
        for(int i = 0; i < unlockedAbilities.length; i++)
            unlockedAbilities[i] = true;
        currentAbilities = new int[maxAbilities];
        for(int i = 0; i < currentAbilities.length; i++)
            currentAbilities[i] = i;
        abilityKeys = defaultKeys;
    }

    public static void initSession() {
        //to be implemented
    }

    public static AbilityType[] getCurrentAbilities() {
        AbilityType[] abilities = new AbilityType[currentAbilities.length];
        for(int i = 0; i < currentAbilities.length; i++) {
            abilities[i] = allAbilities[currentAbilities[i]];
        }
        return abilities;
    }

    public static int getMaxCombo() {
        return maxCombo;
    }

    public static int[] getAbilityKeys() {
        return abilityKeys;
    }
}
