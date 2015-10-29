package com.dd.processbutton.iml;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import com.dd.processbutton.ProcessButton;
import com.dd.processbutton.R;

import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

/*
 *    The MIT License (MIT)
 *
 *   Copyright (c) 2014 Danylyk Dmytro
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

public class ActionProcessButton extends ProcessButton {


    private Mode mMode;

    private int mColor1;
    private int mColor2;
    private int mColor3;
    private int mColor4;

    private ColorDrawable overlay;
    private SmoothProgressDrawable mEndlessProgressDrawable;

    public enum Mode {
        PROGRESS, ENDLESS
    }

    private Rect rect = new Rect();

    public ActionProcessButton(Context context) {
        super(context);
        init(context, null);
    }

    public ActionProcessButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ActionProcessButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        Resources res = context.getResources();

        overlay = new ColorDrawable(Color.argb(80, 0, 0, 0));

        mMode = Mode.ENDLESS;

        mColor1 = res.getColor(R.color.holo_blue_bright);
        mColor2 = res.getColor(R.color.holo_green_light);
        mColor3 = res.getColor(R.color.holo_orange_light);
        mColor4 = res.getColor(R.color.holo_red_light);

        TypedArray attr = getTypedArray(attributeSet, R.styleable.ActionProcessButton);
        if (attr == null) {
            return;
        }

        try {
            mColor1 = attr.getColor(R.styleable.ActionProcessButton_pb_progress_color_1, mColor1);
            mColor2 = attr.getColor(R.styleable.ActionProcessButton_pb_progress_color_2, mColor2);
            mColor3 = attr.getColor(R.styleable.ActionProcessButton_pb_progress_color_3, mColor3);
            mColor4 = attr.getColor(R.styleable.ActionProcessButton_pb_progress_color_4, mColor4);
        } finally {
            attr.recycle();
        }

        SmoothProgressDrawable.Builder builder = (new SmoothProgressDrawable.Builder(context))
                .sectionsCount(2)
                .speed(2f)
                .progressiveStart(true)
                .progressiveStartSpeed(2.5f)
                .progressiveStopSpeed(2.5f)
                .colors(new int[] { mColor1, mColor2, mColor3, mColor4 });
        mEndlessProgressDrawable = builder.build();
        mEndlessProgressDrawable.setCallback(this);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ActionProcessButton button = ActionProcessButton.this;
                int cornerRadius = (int) button.getCornerRadius();
                int strokeWidth = (int) mEndlessProgressDrawable.getStrokeWidth();
                int width = button.getWidth();
                int height = button.getHeight();

                mEndlessProgressDrawable.setBounds(cornerRadius, height-strokeWidth, width, height);

                // Important: remove this listener to not keep receiving callbacks
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    button.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    button.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        boolean res = super.verifyDrawable(who);
        return res || who == mEndlessProgressDrawable;
    }

    public void setMode(Mode mode) {
        mMode = mode;
    }

    public void setColorScheme(int color1, int color2, int color3, int color4) {
        mColor1 = color1;
        mColor2 = color2;
        mColor3 = color3;
        mColor4 = color4;
        mEndlessProgressDrawable.setColors(new int[] { mColor1, mColor2, mColor3, mColor4 });
    }

    @Override
    public void drawProgress(Canvas canvas) {
        if (getBackground() != getNormalDrawable()) {
            setBackgroundCompat(getNormalDrawable());
        }

        switch (mMode) {
            case ENDLESS:
                drawEndlessProgress(canvas);
                break;
            case PROGRESS:
                drawLineProgress(canvas);
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isProgressState() && mMode == Mode.ENDLESS) {
            if (!mEndlessProgressDrawable.isRunning()) {
                mEndlessProgressDrawable.start();
            }
        } else {
            mEndlessProgressDrawable.stop();
        }
    }

    private void drawLineProgress(Canvas canvas) {
        float scale = (float) getProgress() / (float) getMaxProgress();
        float indicatorWidth = (float) getMeasuredWidth() * scale;

        getProgressDrawableBounds(rect);
        overlay.setBounds(rect.left, rect.top, rect.right, rect.bottom);
        overlay.draw(canvas);

        getProgressDrawable().setBounds(rect.left, rect.top, (int) indicatorWidth, rect.bottom);
        getProgressDrawable().draw(canvas);
    }

    private void drawEndlessProgress(Canvas canvas) {
        if (mEndlessProgressDrawable.isRunning()) {
            int state = canvas.save();
            mEndlessProgressDrawable.draw(canvas);
            canvas.restoreToCount(state);
        }
    }

    private void getProgressDrawableBounds(Rect rect) {
        double indicatorHeightPercent = 0.08; // 5%
        int bottom = (int) (getMeasuredHeight() - getMeasuredHeight() * indicatorHeightPercent);
        rect.set(0, bottom, getMeasuredWidth(), getMeasuredHeight());
    }

}