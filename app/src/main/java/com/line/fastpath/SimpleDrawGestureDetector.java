package com.line.fastpath;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by lee on 16/11/25.
 */

public class SimpleDrawGestureDetector {

    private static final int MAX_INTERVAL_FOR_CLICK = ViewConfiguration.getTapTimeout();
    private static int MAX_DISTANCE_FOR_CLICK;

    public interface ISimpleDrawGestureDetector {
        void onFingerDraw(float x, float y, int motionEvent);

        void onClick();
    }

    private float mLastX = 0;
    private float mLastY = 0;
    private boolean mIsWaitUpEvent = false;
    private boolean mIsFirstDrawOn = false;
    private ISimpleDrawGestureDetector iSimpleDrawGestureDetector;

    public SimpleDrawGestureDetector(Context context) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        MAX_DISTANCE_FOR_CLICK = viewConfiguration.getScaledTouchSlop();
    }

    public void setISimpleDrawGestureDetector(ISimpleDrawGestureDetector simpleDrawGestureDetector) {
        this.iSimpleDrawGestureDetector = simpleDrawGestureDetector;
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (this.iSimpleDrawGestureDetector == null) return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mLastY = event.getY();
                mIsWaitUpEvent = true;
                mIsFirstDrawOn = false;
                v.postDelayed(mTimerForUpEvent, MAX_INTERVAL_FOR_CLICK);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - mLastX, dy = event.getY() - mLastY;
                if (Math.abs(dx) > MAX_DISTANCE_FOR_CLICK
                        || Math.abs(dy) > MAX_DISTANCE_FOR_CLICK) {
                    if (mIsWaitUpEvent) {
                        mIsWaitUpEvent = false;
                    }
                }

                if (!mIsWaitUpEvent) {
                    if(!mIsFirstDrawOn) {
                        mIsFirstDrawOn = true;
                        v.removeCallbacks(mTimerForUpEvent);
                        this.iSimpleDrawGestureDetector.onFingerDraw(mLastX, mLastY, MotionEvent.ACTION_DOWN);
                    }

                    this.iSimpleDrawGestureDetector.onFingerDraw(event.getX(), event.getY(), MotionEvent.ACTION_MOVE);
                    mLastX = event.getX();
                    mLastY = event.getY();
                }

                break;
            case MotionEvent.ACTION_UP:
                v.removeCallbacks(mTimerForUpEvent);

                if (mIsWaitUpEvent) {
                    this.iSimpleDrawGestureDetector.onClick();
                } else {
                    this.iSimpleDrawGestureDetector.onFingerDraw(mLastX, mLastY, MotionEvent.ACTION_UP);
                }
                break;
        }
        return true;
    }

    Runnable mTimerForUpEvent = new Runnable() {
        public void run() {
            mIsWaitUpEvent = false;
        }
    };
}
