package com.dwg.egou.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016/4/6.
 */
public class Indicator extends LinearLayout {
    private Paint mPaint;
    private int width3;
    private int position;
    private float positionOffset;
    private float indicatorHeight = 3;

    public Indicator(Context context) {
        super(context);
        init();
    }

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Indicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void init()
    {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        width3 = dm.widthPixels;
        setBackgroundColor(Color.TRANSPARENT);//必须设置背景  否则onDraw不会执行。
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("执行了onDraw函数","True");
        int mleft = (int) ((position+positionOffset)*width3/3);
        canvas.drawRect(40+mleft, 0, width3/3-40+mleft, indicatorHeight,mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        super.onDraw(canvas);
    }
    public void scoll(int position,float positionOffset)
    {
        this.position = position;
        this.positionOffset = positionOffset;
        Log.e("Indicator","position = "+position+ "  positionOffset = "+positionOffset );
        invalidate();
    }
}