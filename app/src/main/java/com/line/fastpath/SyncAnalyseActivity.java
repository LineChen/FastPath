package com.line.fastpath;

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

/**
 * Created by chenliu on 2019-12-16.
 */
public class SyncAnalyseActivity extends AppCompatActivity {

    private static final String TAG = "Fast-Path";
    private DrawView finDrawView;
    private DrawView recoverDrawView;
    private TextView tvResult;

    SyncPathAnalyse fastPathAnalyse = new SyncPathAnalyse();
    private List<Point> allPoints = new ArrayList<>();
    private List<Point> zipPoints = new ArrayList<>();

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

        fastPathAnalyse.setAnalyseCallback(new SyncPathAnalyse.AnalyseCallback() {
            @Override
            public void onAnalyseComplete(List<Point> result) {
                int size = result.size();
                for (Point p : result) {
                    switch (p.action) {
                        case MotionEvent.ACTION_DOWN:
                            recoverpaintDrawStart(p.x, p.y);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            recoverpaintDrawMove(p.x, p.y);
                            break;
                        case MotionEvent.ACTION_UP:
                            recoverpaintDrawMove(p.x, p.y);
                            recoverDrawView.invalidate();
                            break;
                    }
                }

                if (size > 0) {
                    zipPoints.addAll(result);
                }
            }
        });

        SimpleDrawGestureDetector drawGestureDetector = new SimpleDrawGestureDetector(this);
        drawGestureDetector.setISimpleDrawGestureDetector(new SimpleDrawGestureDetector.ISimpleDrawGestureDetector() {
            @Override
            public void onFingerDraw(float x, float y, int motionEvent) {
                x /= boardWidth;
                y /= boardHeight;
                Log.d(TAG, "onFingerDraw: " + String.format("(%f,%f) , motionEvent: %d", x, y, motionEvent));
                fastPathAnalyse.recordPoint(x, y, motionEvent);
                allPoints.add(new Point(x, y, motionEvent));
                if (motionEvent == MotionEvent.ACTION_DOWN) {
                    finDrawView.paintDrawStart(x, y);
                } else if (motionEvent == MotionEvent.ACTION_MOVE) {
                    finDrawView.paintDrawMove(x, y);
                    finDrawView.invalidate();
                } else if (motionEvent == MotionEvent.ACTION_UP) {
                    tvResult.append(getString(R.string.zip_result, allPoints.size(), zipPoints.size()));
                    finDrawView.invalidate();
//                    finDrawView.paintKeyPoint(allPoints, Color.RED);
                    allPoints.clear();
//                    recoverDrawView.paintKeyPoint(zipPoints);
                    zipPoints.clear();
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
