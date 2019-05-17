package com.fireball.game.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.LinkedList;

public abstract class View {
    public int priority = 0;
    protected int width, height;
    protected boolean isFocused = false;

    protected View parentView;
    protected LinkedList<View> subViews = new LinkedList<View>();

    public View(View parentView, int width, int height) {
        this.parentView = parentView;
        if(parentView != null)
            parentView.addSubView(this);

        this.width = width;
        this.height = height;
    }

    public abstract void update(double delta);

    public abstract void preDraw();
    public abstract void draw(SpriteBatch batch);

    public void addSubView(View v) {
        subViews.add(v);
        sortSubViews();
    }

    public void removeSubView(View v) {
        for(int i = 0; i < subViews.size(); i++) {
            if(v.equals(subViews.get(i))) {
                subViews.remove(i--).dispose();
            }
        }
    }

    protected void updateSubViews(double delta) {
        for(View v: subViews) {
            v.update(delta);
        }
    }

    protected void preDrawSubViews() {
        for(View v: subViews) {
            v.preDraw();
        }
    }

    protected void drawSubViews(SpriteBatch batch) {
        for(View v: subViews) {
            v.draw(batch);
        }
    }

    protected void unfocusAllSubViews() {
        for(View v: subViews) {
            v.setFocused(false);
        }
    }

    public abstract void processViewAction(String action);
    protected void sendViewAction(String action) {
        if(parentView != null)
            parentView.processViewAction(action);
    }

    private void sortSubViews() {
        for(int i = 0; i < subViews.size()-1; i++) {
            for(int j = 0; j < i; j++) {
                if(subViews.get(j).priority < subViews.get(j+1).priority) {
                    subViews.add(j, subViews.remove(j+1));
                }
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void dispose() {
        for(View v: subViews) {
            v.dispose();
        }
    }
}
