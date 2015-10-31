package com.dd.processbutton;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;

public class BackgroundBuilder {

    private Context context;

    public BackgroundBuilder(Context context) {
        this.context = context;
    }

    public Drawable createBackground(AttributeSet attributeSet) {
        TypedArray attr = getTypedArray(attributeSet, R.styleable.FlatButton);
        if (attr == null) {
            return null;
        }

        try {
            float defValue = getDimension(R.dimen.corner_radius);
            int cornerRadius = (int) attr.getDimension(R.styleable.FlatButton_pb_cornerRadius, defValue);

            if (Build.VERSION.SDK_INT >= 21) {
                return setupDrawableV21(attr, cornerRadius);
            } else {
                return setupDrawable(attr, cornerRadius);
            }
        } finally {
            attr.recycle();
        }
    }

    private Drawable setupDrawable(TypedArray attr, int cornerRadius) {
        ColorStateList tintStateList = context.getResources().getColorStateList(R.color.btn_tint_selector);
        GradientDrawable roundedRectDrawable = (GradientDrawable) getDrawable(R.drawable.rounded_rect).mutate();

        roundedRectDrawable.setCornerRadius(cornerRadius);
        Drawable drawable = DrawableCompat.wrap(roundedRectDrawable);
        DrawableCompat.setTintList(drawable, tintStateList);
        return drawable;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Drawable setupDrawableV21(TypedArray attr, int cornerRadius) {
        ColorStateList tintStateList = context.getResources().getColorStateList(R.color.btn_tint_selector);
        RippleDrawable rippleDrawable = (RippleDrawable) getDrawable(R.drawable.rounded_rect);
        GradientDrawable roundedRectDrawable = (GradientDrawable) rippleDrawable.getDrawable(0);

        roundedRectDrawable.setCornerRadius(cornerRadius);
        rippleDrawable.setTintList(tintStateList);
        return rippleDrawable;
    }

    public float getDimension(int id) {
        return context.getResources().getDimension(id);
    }

    @SuppressLint("NewApi")
    public static void setColor(GradientDrawable drawable, TypedArray attr, int index, int defaultColor) {
        ColorStateList stateList = getColor(attr, index, defaultColor);
        if (Build.VERSION.SDK_INT >= 21) {
            drawable.setColor(stateList);
        } else {
            drawable.setColor(stateList.getDefaultColor());
        }
    }

    public static ColorStateList getColor(TypedArray attr, int index, int defaultColor) {
        ColorStateList result = null;
        if (attr.hasValue(index)) {
            result = attr.getColorStateList(index);
        }

        if (result == null) {
            result = ColorStateList.valueOf(defaultColor);
        }

        return result;
    }

    public int getColor(int id) {
        return context.getResources().getColor(id);
    }

    public TypedArray getTypedArray(AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    public Drawable getDrawable(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void setBackgroundCompat(View view, Drawable drawable) {
        int pL = view.getPaddingLeft();
        int pT = view.getPaddingTop();
        int pR = view.getPaddingRight();
        int pB = view.getPaddingBottom();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
        view.setPadding(pL, pT, pR, pB);
    }
}
