package com.fireball.game.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.fireball.game.textures.TextureData;
import com.fireball.game.textures.TextureManager;

import java.util.HashMap;

public class Util {
    public static double skewPctPow(double pct, double pow) {
        return skewPctPow(pct, pow, pow);
    }

    public static double skewPctPow(double pct, double powL, double powR) {
        if(pct < 0.5) {
            return Math.pow(pct * 2, powL) * 0.5;
        } else {
            return 1 - (Math.pow((1 - pct) * 2, powR) * 0.5);
        }
    }

    public static double mix(double a, double b, double pct) {
        return (a * (1 - pct)) + (b * pct);
    }

    public static int choose(int... ints) {
        return ints[(int)(ints.length * Math.random())];
    }

    public static NinePatch createNinePatch(TextureData data, float cornerSize) {
        Texture texture = TextureManager.getTexture(data);
        NinePatch sprite = new NinePatch(texture, texture.getWidth()/3, texture.getWidth()/3, texture.getHeight()/3, texture.getHeight()/3);
        sprite.setBottomHeight(cornerSize);
        sprite.setTopHeight(cornerSize);
        sprite.setLeftWidth(cornerSize);
        sprite.setRightWidth(cornerSize);
        return sprite;
    }

    public static Color makeGray(float f, float a) {
        return new Color(f, f, f, a);
    }


    //returns a weighted random number with an average of 1, and a distribution of y=x^weight
    private static HashMap<Double, Double> averageValues = new HashMap<Double, Double>();
    public static double weightedRandom(double weight) {
        Double average = averageValues.get(weight);
        if(average == null) {
            average = weightedAverage(weight);
            averageValues.put(weight, average);
        }

        return Math.pow(Math.random(), weight) / average;
    }
    private static double weightedAverage(double weight) {
        double total = 0;
        for(int i = 0; i < 10000; i++) {
            total += Math.pow(Math.random(), weight);
        }
        return total / 10000;
    }
}
