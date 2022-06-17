package com.line.fastpath.drawview.brush.paint;

/**
 * Created by zhangjian on 2020/11/23 in project jyxb_mobile.
 */
public class PaintStyle {
    private String color;
    private int width;

    public PaintStyle() {
    }

    public PaintStyle(String color, int width) {
        this.color = color;
        this.width = width;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
