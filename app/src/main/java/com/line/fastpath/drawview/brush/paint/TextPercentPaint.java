package com.line.fastpath.drawview.brush.paint;

import android.text.TextPaint;

/**
 * Created by zj on 2020/3/24 in project jiayouxueba_3.X.
 */
public class TextPercentPaint extends TextPaint {
    private float scale = 1;

    public TextPercentPaint(float scale) {
        this.scale = scale;
    }

    public TextPercentPaint(PercentPaint paint) {
        super(paint);
        this.scale = paint.getScale();
    }

    private int faceTextSize = 0;

    public int getFaceTextSize() {
        return faceTextSize;
    }

    public void setFaceTextSize(int faceTextSize) {
        this.faceTextSize = faceTextSize;
        setTextSize(faceTextSize * scale);
    }

    private int faceStrokeWidth = 0;

    public void setFaceStrokeWidth(int faceStrokeWidth) {
        this.faceStrokeWidth = faceStrokeWidth;
        setStrokeWidth(faceStrokeWidth * scale);
    }

    public int getFaceStrokeWidth() {
        return faceStrokeWidth;
    }
}
