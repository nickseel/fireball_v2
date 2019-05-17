package com.fireball.game.util;

public class Settings {
    public static int[] current;
    private static final int[] defaults = {
            1400,   //RESOLUTION_WIDTH
            900,    //RESOLUTION_HEIGHT
            0,      //IS_FULLSCREEN
            0,      //USE_EFFICIENCY_METRICS
    };

    public static int get(SettingType s) {
        return current[s.getIndex()];
    }

    public static void loadSettings() {
        if(false) {
            //check for settings file before initiating to defaults
        } else {
            //set settings to defaults
            resetSettings();
        }
    }

    public static void resetSettings() {
        current = new int[defaults.length];
        System.arraycopy(defaults, 0, current, 0, defaults.length);
    }

    public static void saveSettings() {

    }
}
