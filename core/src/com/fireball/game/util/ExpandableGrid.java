package com.fireball.game.util;

import java.util.ArrayList;

public class ExpandableGrid<T> {
    private ArrayList<ArrayList<T>> grid;

    private T defaultValue;

    private int width, height;
    private int centerX, centerY;

    public ExpandableGrid() {
        this(null);
    }

    public ExpandableGrid(T defaultValue) {
        grid = new ArrayList<ArrayList<T>>();
        this.defaultValue = defaultValue;
        width = 0;
        height = 0;
        centerX = 0;
        centerY = 0;
    }

    public void add(T data, int x, int y) {
        x += centerX;
        y += centerY;

        while(y >= height) {
            ArrayList<T> newRow = new ArrayList<T>();
            for(int i = 0; i < width; i++) {
                newRow.add(defaultValue);
            }
            grid.add(newRow);
            height++;
        }
        while(y < 0) {
            ArrayList<T> newRow = new ArrayList<T>();
            for(int i = 0; i < width; i++) {
                newRow.add(defaultValue);
            }
            grid.add(0, newRow);
            height++;
            centerY++;
            y++;
        }
        while(x >= width) {
            for(int i = 0; i < height; i++) {
                grid.get(i).add(defaultValue);
            }
            width++;
        }
        while(x < 0) {
            for(int i = 0; i < height; i++) {
                grid.get(i).add(0, defaultValue);
            }
            width++;
            centerX++;
            x++;
        }

        grid.get(y).set(x, data);
    }

    public T get(int x, int y) {
        if(x + centerX < 0 || x + centerX >= width || y + centerY < 0 || y + centerY >= height)
            return defaultValue;
        return grid.get(y + centerY).get(x + centerX);
    }

    public T[][] toArray() {
        T[][] array = (T[][])new Object[width][height];
        for(int r = 0; r < height; r++) {
            ArrayList<T> row = grid.get(r);

            for(int c = 0; c < width; c++) {
                array[r][c] = row.get(c);
            }
        }
        return array;
    }

    public String toString() {
        String s = "";
        for(int r = 0; r < height; r++) {
            ArrayList<T> row = grid.get(r);

            for(int c = 0; c < width; c++) {
                s += row.get(c) + ", ";
            }
            s += "\n";
        }
        return s;
    }
}
