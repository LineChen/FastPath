package com.line.fastpath.drawview.brush.paint;

import android.graphics.Paint;

/**
 * Created by zj on 2020/3/24 in project jiayouxueba_3.X.
 */
public class PercentPaint extends Paint {

    private float scale;

    public PercentPaint(float scale) {
        this.scale = scale;
    }

    public PercentPaint() {
        this(1);
    }

    public PercentPaint(PercentPaint paint) {
        super(paint);
        this.scale = paint.scale;
        this.faceStrokeWidth = paint.faceStrokeWidth;
    }

    private float faceStrokeWidth = 0;

    public void setFaceStrokeWidth(float faceStrokeWidth) {
        this.faceStrokeWidth = faceStrokeWidth;
        setStrokeWidth(faceStrokeWidth * scale);
    }


    public float getFaceStrokeWidth() {
        return faceStrokeWidth;
    }

    public float getScale() {
        return scale;
    }
}
