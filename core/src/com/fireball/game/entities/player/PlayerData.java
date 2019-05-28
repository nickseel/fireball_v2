package com.fireball.game.entities.player;

public class PlayerData {
    public static final int maxAbilities = 4;
    private static final int[] defaultKeys = new int[] {1, 2, 3, 4};

    private static int maxCombo;
    private static Ability[] allAbilities;
    private static boolean[] unlockedAbilities;
    private static int[] currentAbilities;
    private static int[] abilityKeys;

    public void initDefault() {
        maxCombo = 2;

        allAbilities = Ability.getAbilities();
        unlockedAbilities = new boolean[allAbilities.length];
        for(int i = 0; i < unlockedAbilities.length; i++)
            unlockedAbilities[i] = true;
        currentAbilities = new int[maxAbilities];
        for(int i = 0; i < currentAbilities.length; i++)
            currentAbilities[i] = i;
        abilityKeys = defaultKeys;
    }

    public void initSession() {
        //to be implemented
    }
}
