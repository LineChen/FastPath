package com.line.fastpath;


import android.util.Log;

import androidx.annotation.NonNull;

import com.line.lib.DP;
import com.line.lib.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenliu on 2019-12-13.
 */
public class FastPathAnalyse {
    private static final String TAG = "FastPathAnalyse";

    private float DELTA = 0.1f;

    private List<Point> pointList = new ArrayList<>();

    public void recordPoint(float x, float y) {
        pointList.add(new Point(x, y));
    }

    public @NonNull
    List<Point> analyse() {
        long start = System.currentTimeMillis();
        Log.e(TAG, "================analyse start=================");
        Log.e(TAG, "result 原始大小:" + pointList.size());
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

        result = DP.dpData(pointList, 0.002f);
        Log.e(TAG, "================analyse end ================= " + (System.currentTimeMillis() - start) + "ms");
        Log.e(TAG, "result 原始大小:" + pointList.size() + ",压缩后大小:" + result.size());
        pointList.clear();
        return result;
    }


}
