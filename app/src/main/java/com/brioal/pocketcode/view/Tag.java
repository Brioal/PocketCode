package com.brioal.pocketcode.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

import com.brioal.pocketcode.R;

/**
 * 一个带边框的TextView
 * Created by Brioal on 2016/5/19.
 */

public class Tag extends TextView {
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mStrikeWidth;
    private int mColor;

    public Tag(Context context) {
        this(context, null);
    }

    public Tag(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mStrikeWidth = 2;
        mColor = getContext().getResources().getColor(R.color.colorPrimary);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrikeWidth);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        canvas.drawRoundRect(new RectF(mStrikeWidth, mStrikeWidth, mWidth - mStrikeWidth, mHeight - mStrikeWidth), 5, 5, mPaint);
    }
}
