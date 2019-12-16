package com.line.lib;

/**
 * Created by chenliu on 2019-12-16.
 */
public class Point {
    public float x;
    public float y;
    private boolean important = true;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }
}
