package com.line.fastpath;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.line.fastpath.drawview.DrawView;
import com.line.fastpath.drawview.SimpleDrawGestureDetector;
import com.line.lib.Point;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Fast-Path";
    private DrawView finDrawView;
    private DrawView recoverDrawView;
    private TextView tvResult;

    FastPathAnalyse fastPathAnalyse = new FastPathAnalyse();
    private List<Point> allPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        finDrawView = findViewById(R.id.fin_draw_view);
        recoverDrawView = findViewById(R.id.recover_draw_view);
        tvResult = findViewById(R.id.tv_result);

        final int boardWidth = getResources().getDisplayMetrics().widthPixels;
        final int boardHeight = 600;
        finDrawView.init(boardWidth, boardHeight);
        recoverDrawView.init(boardWidth, boardHeight);
        SimpleDrawGestureDetector drawGestureDetector = new SimpleDrawGestureDetector(this);
        drawGestureDetector.setISimpleDrawGestureDetector(new SimpleDrawGestureDetector.ISimpleDrawGestureDetector() {
            @Override
            public void onFingerDraw(float x, float y, int motionEvent) {
                x /= boardWidth;
                y /= boardHeight;
                Log.d(TAG, "onFingerDraw: " + String.format("(%f,%f) , motionEvent: %d", x, y, motionEvent));
                fastPathAnalyse.recordPoint(x, y);
                allPoints.add(new Point(x, y, motionEvent));
                if (motionEvent == MotionEvent.ACTION_DOWN) {
                    finDrawView.paintDrawStart(x, y);
                } else if (motionEvent == MotionEvent.ACTION_MOVE) {
                    finDrawView.paintDrawMove(x, y);
                    finDrawView.invalidate();
                } else if (motionEvent == MotionEvent.ACTION_UP) {
                    finDrawView.invalidate();
                    List<Point> analyseResult = fastPathAnalyse.analyse();
                    int size = analyseResult.size();
                    if (size > 0) {
                        Point pOrigin = analyseResult.get(0);
                        recoverpaintDrawStart(pOrigin.x, pOrigin.y);
                        for (int i = 1; i < size; i++) {
                            Point pointF = analyseResult.get(i);
                            recoverpaintDrawMove(pointF.x, pointF.y);
                        }
                        recoverDrawView.paintKeyPoint(analyseResult);
                        finDrawView.paintKeyPoint(allPoints, Color.RED);
                        tvResult.append(getString(R.string.zip_result, allPoints.size(), size));
                        allPoints.clear();

                    }
                }
            }

            @Override
            public void onClick() {
                Log.d(TAG, "onClick: just click");
            }
        });
        finDrawView.setSimpleDrawGestureDetector(drawGestureDetector);
        finDrawView.setOnTouchListener(finDrawView);

    }


    private void recoverpaintDrawStart(float x, float y) {
        recoverDrawView.paintDrawStart(x, y);
    }

    private void recoverpaintDrawMove(float x, float y) {
        recoverDrawView.paintDrawMove(x, y);
        recoverDrawView.invalidate();
    }

    public void reset(View view) {
        finDrawView.clear();
        finDrawView.invalidate();
        recoverDrawView.clear();
        recoverDrawView.invalidate();
        tvResult.setText("");
    }
}
