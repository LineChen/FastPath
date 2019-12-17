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

    private static final int DELAY = 300;

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

    public List<Point> analyse(List<Point> data) {
        long start = System.currentTimeMillis();
        Log.e(TAG, "================analyse start=================");
        Log.e(TAG, "result 原始大小:" + data.size());
        List<Point> result = new ArrayList<>();
//        if (pointList.size() > 2) {
//            DT.Point pOrigin = pointList.get(0);
//            result.add(pOrigin);
//            int size = pointList.size();
//            for (int i = 1; i < size; i++) {
//                DT.Point pIndex = pointList.get(i);
//                float slope = (pIndex.y - pOrigin.y) / (pIndex.x - pOrigin.x);
//                Log.d(TAG, "analyse: slope = " + slope + pIndex.toString() + pOrigin.toString());
//                if (slope > DELTA) {
//                    result.add(pIndex);
//                    pOrigin = pIndex;
//                }
//            }
//            result.add(pointList.get(size - 1));
//        }

//        result = DT.douglasData(pointList);

        result = DP.dpData(data, 0.002f);
        if (analyseCallback != null) {
            analyseCallback.onAnalyseComplete(result);
        }
        Log.e(TAG, "================analyse end ================= " + (System.currentTimeMillis() - start) + "ms");
        Log.e(TAG, "result 原始大小:" + data.size() + ",压缩后大小:" + result.size());
        return result;
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
        handler.post(looperRunnable);
    }

    private void stopLoop() {
        handler.removeCallbacks(looperRunnable);
    }


    public interface AnalyseCallback {
        void onAnalyseComplete(List<Point> result);
    }
}
