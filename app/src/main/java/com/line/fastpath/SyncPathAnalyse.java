package com.line.fastpath;


import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.line.lib.DP;
import com.line.lib.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by chenliu on 2019-12-13.
 */
public class SyncPathAnalyse {

    private static final String TAG = "FastPathAnalyse";

    private static final int DELAY = 100;

    /**
     * 阀值
     */
    private static final float THRESHOLD = 0.000f;


    private Handler handler = new Handler();

    private AnalyseCallback analyseCallback;

    private LinkedBlockingQueue<Point> pointLinkedBlockingQueue = new LinkedBlockingQueue<>();
    private LooperRunnable looperRunnable = new LooperRunnable();

    public void recordPoint(float x, float y, int action) {
        pointLinkedBlockingQueue.add(new Point(x, y, action));
        if (action == MotionEvent.ACTION_DOWN) {
            startLoop();
        } else if (action == MotionEvent.ACTION_UP) {
            stopLoop();
            doAnalyse();
        }
    }

    public void setAnalyseCallback(AnalyseCallback analyseCallback) {
        this.analyseCallback = analyseCallback;
    }

    public void analyse(List<Point> data) {
        long start = System.currentTimeMillis();
        Log.e(TAG, "================analyse start=================");
        Log.e(TAG, "result 原始大小:" + data.size());
        List<Point> result = DP.dpData(data, THRESHOLD);
        if (analyseCallback != null) {
            analyseCallback.onAnalyseComplete(result);
        }
        Log.e(TAG, "================analyse end ================= " + (System.currentTimeMillis() - start) + "ms");
        Log.e(TAG, "result 原始大小:" + data.size() + ",压缩后大小:" + result.size());
    }


    private class LooperRunnable implements Runnable {
        @Override
        public void run() {
            doAnalyse();
            handler.postDelayed(this, DELAY);
        }
    }

    private void doAnalyse() {
        if (pointLinkedBlockingQueue.size() > 0) {
            List<Point> data = new ArrayList<>(pointLinkedBlockingQueue);
            analyse(data);
            pointLinkedBlockingQueue.clear();
        }
    }

    private void startLoop() {
        handler.removeCallbacks(looperRunnable);
        handler.postDelayed(looperRunnable, DELAY);
    }

    private void stopLoop() {
        handler.removeCallbacks(looperRunnable);
    }


    public interface AnalyseCallback {
        void onAnalyseComplete(List<Point> result);
    }
}
