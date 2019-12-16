package com.line.fastpath;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public abstract class IDrawView extends View {

    public IDrawView(Context context) {
        super(context);
    }

    public IDrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void  paintDrawStart(float x, float y);
    public abstract void paintDrawMove(float x, float y);
    public abstract void init(int w, int h);
    public abstract void setPaintColor(String color);
}
