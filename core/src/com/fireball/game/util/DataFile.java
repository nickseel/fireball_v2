package com.fireball.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;

public class DataFile {
    private static HashMap<String, JsonValue> jsonFiles = new HashMap<String, JsonValue>();
    private static JsonReader jsonReader = new JsonReader();
    private static JsonValue currentLocation;

    public static void loadJsonFile(String file) {
        jsonFiles.put(file, jsonReader.parse(Gdx.files.internal("files/" + file + ".json")));
    }

    public static void setCurrentLocation(String file, String... keys) {
        JsonValue value = jsonFiles.get(file);
        for(int i = 0; i < keys.length; i++) {
            value = value.get(keys[i]);
        }
        currentLocation = value;
    }

    public static int getInt(String... keys) {
        return getPenultimate(keys).getInt(keys[keys.length-1]);
    }

    public static int getInt(String name) {
        return currentLocation.getInt(name);
    }

    public static float getFloat(String... keys) {
        return getPenultimate(keys).getFloat(keys[keys.length-1]);
    }

    public static float getFloat(String name) {
        return currentLocation.getFloat(name);
    }

    public static String getString(String... keys) {
        return getPenultimate(keys).getString(keys[keys.length-1]);
    }

    public static String getString(String name) {
        return currentLocation.getString(name);
    }

    public static int[] getIntArray(String... keys) {
        return getUltimate(keys).asIntArray();
    }

    public static int[] getIntArray(String name) {
        return getUltimate(name).asIntArray();
    }

    private static JsonValue getPenultimate(String... keys) {
        JsonValue value = currentLocation;
        for(int i = 0; i < keys.length-1; i++) {
            value = value.get(keys[i]);
        }
        return value;
    }

    private static JsonValue getUltimate(String... keys) {
        JsonValue value = currentLocation;
        for(int i = 0; i < keys.length; i++) {
            value = value.get(keys[i]);
        }
        return value;
    }
}
