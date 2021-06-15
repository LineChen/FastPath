package com.line.fastpath.drawview;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.line.lib.Point;

import java.util.List;


/**
 * Created by lee on 16/11/9.
 * <p>
 * 回放数据 白板绘制部分
 */
public class DrawViewSleek extends IDrawView implements View.OnTouchListener {
    public static final String TAG = "ERASER_TAG";
    private Bitmap mBitmap;
    private Canvas mCanvas;

    private Paint penPaint; // 当前的笔
    private int paintColor = Color.rgb(100, 100, 100); // 当前笔的颜色
    private static final int PAINT_SIZE = 2;
    private int mWidth;
    private int mHeight;

    private float lastX, lastY;

    private Paint keyPointPaint;

    private SimpleDrawGestureDetector simpleDrawGestureDetector; // 手势书写

    public DrawViewSleek(Context context) {
        this(context, null);
    }

    public DrawViewSleek(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        penPaint = new Paint();
        setDrawPaint(penPaint, PAINT_SIZE);
        penPaint.setColor(paintColor);

        keyPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        setDrawPaint(keyPointPaint, PAINT_SIZE);
        keyPointPaint.setStyle(Paint.Style.FILL);
        keyPointPaint.setColor(Color.BLUE);
        keyPointPaint.setStrokeWidth(6);
    }

    public void init(int w, int h) {
        mWidth = w;
        mHeight = h;

        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (this.simpleDrawGestureDetector != null) {
            return simpleDrawGestureDetector.onTouch(v, event);
        }
        return super.onTouchEvent(event);
    }

    private void setDrawPaint(Paint pen, int size) {
        pen.setAntiAlias(true);
        pen.setDither(true);
        pen.setStrokeWidth(size);
        pen.setStyle(Paint.Style.STROKE);
        pen.setStrokeJoin(Paint.Join.ROUND);
        pen.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setSimpleDrawGestureDetector(SimpleDrawGestureDetector drawGestureDetector) {
        this.simpleDrawGestureDetector = drawGestureDetector;
    }

    /**
     * 获取 画布
     *
     * @return
     */
    public Bitmap getBitmap() {
        return mBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    /**
     * 设置笔的颜色
     *
     * @param color
     */
    public void setPaintColor(String color) {
        if (this.paintColor == Color.parseColor(color)) return;

        this.paintColor = Color.parseColor(color);
        penPaint.setColor(this.paintColor);
    }

    /**
     * 清空所有笔记
     */
    public void clear() {
        mBitmap.eraseColor(Color.TRANSPARENT);
    }


    private Path currentPath = new Path();

    public void paintDrawStart(float x, float y) {
        lastX = toMX(x);
        lastY = toMY(y);
        currentPath.reset();
        currentPath.moveTo(lastX, lastY);
    }

    public void paintDrawMove(float x, float y) {
        float toMX = toMX(x);
        float stopY = toMY(y);
//        mCanvas.drawLine(lastX, lastY, toMX, stopY, penPaint);
        currentPath.quadTo((lastX + toMX) / 2, (lastY + stopY) / 2, toMX, stopY);
        mCanvas.drawPath(currentPath, penPaint);

        lastX = toMX;
        lastY = stopY;

    }

    public void paintDrawUp() {
    }

    public void paintKeyPoint(List<Point> keyPoint) {
        for (Point p : keyPoint) {
            float toMX = toMX(p.x);
            float stopY = toMY(p.y);
            mCanvas.drawPoint(toMX, stopY, keyPointPaint);
        }
        invalidate();
    }

    public void paintKeyPoint(List<Point> keyPoint, int color) {
        keyPointPaint.setColor(color);
        paintKeyPoint(keyPoint);
    }


    private float toMX(float f) {
        return f * mWidth;
    }

    private float toMY(float f) {
        return f * mHeight;
    }


}
