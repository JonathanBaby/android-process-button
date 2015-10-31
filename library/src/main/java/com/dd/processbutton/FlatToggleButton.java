package com.dd.processbutton;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.dd.processbutton.utils.DrawableUtils;

public class FlatToggleButton extends ToggleButton {

    private Drawable mDrawable;
    private CharSequence mNormalText;
    private float cornerRadius;
    private BackgroundBuilder backgroundBuilder;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlatToggleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public FlatToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public FlatToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FlatToggleButton(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        backgroundBuilder = new BackgroundBuilder(context);
        if (attrs != null) {
            initAttributes(attrs);
        } else {
            mDrawable = getBackground();
        }

        mNormalText = getText().toString();
        setBackgroundCompat(mDrawable);
    }

    private void initAttributes(AttributeSet attributeSet) {
        TypedArray attr = backgroundBuilder.getTypedArray(attributeSet, R.styleable.FlatButton);
        if (attr == null) {
            return;
        }

        try {
            float defValue = backgroundBuilder.getDimension(R.dimen.corner_radius);
            cornerRadius = attr.getDimension(R.styleable.FlatButton_pb_cornerRadius, defValue);
            mDrawable = backgroundBuilder.createBackground(attributeSet);
        } finally {
            attr.recycle();
        }
    }


    public float getCornerRadius() {
        return cornerRadius;
    }

    public Drawable getNormalDrawable() {
        return mDrawable;
    }

    public CharSequence getNormalText() {
        return mNormalText;
    }

    protected float getDimension(int id) {
        return backgroundBuilder.getDimension(id);
    }

    @SuppressLint("NewApi")
    protected void setColor(GradientDrawable drawable, TypedArray attr, int index, int defaultColor) {
        DrawableUtils.setColorFromAttribute(drawable, attr, index, defaultColor);
    }

    protected ColorStateList getColor(TypedArray attr, int index, int defaultColor) {
        return DrawableUtils.getColorFromAttribute(attr, index, defaultColor);
    }

    protected int getColor(int id) {
        return backgroundBuilder.getColor(id);
    }

    protected TypedArray getTypedArray(AttributeSet attributeSet, int[] attr) {
        return backgroundBuilder.getTypedArray(attributeSet, attr);
    }

    protected Drawable getDrawable(int id) {
        return backgroundBuilder.getDrawable(id);
    }

    /**
     * Set the View's background. Masks the API changes made in Jelly Bean.
     *
     * @param drawable
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void setBackgroundCompat(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }

}
