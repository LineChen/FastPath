package com.line.fastpath.drawview.brush;


/**
 * @author shiming
 * @version v1.0 create at 2017/8/24
 * @des 每个点的控制，关心三个因素：笔的宽度，坐标
 */
public class ControllerPoint {
    public float x;
    public float y;

    public float width;

    public ControllerPoint() {
    }

    public ControllerPoint(float x, float y, float width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    public void set(float x, float y, float w) {
        this.x = x;
        this.y = y;
        this.width = w;
    }


    public void set(ControllerPoint point) {
        this.x = point.x;
        this.y = point.y;
        this.width = point.width;
    }


    public String toString() {
        String str = "X = " + x + "; Y = " + y + "; W = " + width;
        return str;
    }


}
