package com.line.fastpath.drawview.brush;

/**
 * @author shiming
 * @version v1.0 create at 2017/8/24
 * @des 对点的位置和宽度控制的bezier曲线，主要是两个点，都包含了宽度和点的坐标
 */
public class Bezier {
    //控制点的，
    private ControllerPoint mControl = new ControllerPoint();
    //终点
    private ControllerPoint mDestination = new ControllerPoint();
    //下一个需要控制点
    private ControllerPoint mNextControl = new ControllerPoint();
    //资源的点
    private ControllerPoint mSource = new ControllerPoint();


    public void reset() {
        mSource = null;
    }

    public boolean isEmpty(){
        return mSource == null;
    }

    /**
     * 初始化两个点，
     *
     * @param lastX     最后的点的信息
     * @param lastY     最后的点的信息
     * @param lastWidth 最后的点的信息
     * @param x         当前点的信息,当前点的信息
     * @param y
     * @param width     同时这个当前点的宽度是经过计算的得出的
     */
    public void init(float lastX, float lastY, float lastWidth, float x, float y, float width) {
        mSource = new ControllerPoint();
        //资源点设置，最后的点的为资源点
        mSource.set(lastX, lastY, lastWidth);
        float xMid = getMid(lastX, x);
        float yMid = getMid(lastY, y);
        float wMid = getMid(lastWidth, width);
        //距离点为平均点
        mDestination.set(xMid, yMid, wMid);
        //控制点为当前的距离点
        mControl.set(getMid(lastX, xMid), getMid(lastY, yMid), getMid(lastWidth, wMid));
        //下个控制点为当前点
        mNextControl.set(x, y, width);
    }

    /**
     * 替换就的点，原来的距离点变换为资源点，控制点变为原来的下一个控制点，距离点取原来控制点的和新的的一半
     * 下个控制点为新的点
     *
     * @param x     新的点的坐标
     * @param y     新的点的坐标
     * @param width
     */
    public void addNode(float x, float y, float width) {
        mSource.set(mDestination);
        mControl.set(mNextControl);
        mDestination.set(getMid(mNextControl.x, x), getMid(mNextControl.y, y), getMid(mNextControl.width, width));
        mNextControl.set(x, y, width);
    }

    /**
     * @param t 孔子
     * @return
     */
    public ControllerPoint getPoint(double t) {
        float x = (float) getX(t);
        float y = (float) getY(t);
        float w = (float) getW(t);
        ControllerPoint point = new ControllerPoint();
        point.set(x, y, w);
        return point;
    }

    /**
     * 三阶曲线的控制点
     *
     * @param p0
     * @param p1
     * @param p2
     * @param t
     * @return
     */
    private double getValue(double p0, double p1, double p2, double t) {
        double A = p2 - 2 * p1 + p0;
        double B = 2 * (p1 - p0);
        double C = p0;
        return A * t * t + B * t + C;
    }

    private double getX(double t) {
        return getValue(mSource.x, mControl.x, mDestination.x, t);
    }

    private double getY(double t) {
        return getValue(mSource.y, mControl.y, mDestination.y, t);
    }

    private double getW(double t) {
        return getWidth(mSource.width, mNextControl.width, t);
    }

    /**
     * @param x1 一个点的x
     * @param x2 一个点的x
     * @return
     */
    private float getMid(float x1, float x2) {
        return (float) ((x1 + x2) / 2.0);
    }

    private double getWidth(double w0, double w1, double t) {
        return w0 + (w1 - w0) * t;
    }

}
