package com.dd.processbutton.utils;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;

public class DrawableUtils {

    @SuppressLint("NewApi")
    public static void setColorFromAttribute(GradientDrawable drawable, TypedArray attr, int attributeIndex, int defaultColor) {
        ColorStateList stateList = getColorFromAttribute(attr, attributeIndex, defaultColor);
        if (Build.VERSION.SDK_INT >= 21) {
            drawable.setColor(stateList);
        } else {
            drawable.setColor(stateList.getDefaultColor());
        }
    }

    public static ColorStateList getColorFromAttribute(TypedArray attr, int attributeIndex, int defaultColor) {
        ColorStateList result = null;
        if (attr.hasValue(attributeIndex)) {
            result = attr.getColorStateList(attributeIndex);
        }
        if (result == null) {
            result = ColorStateList.valueOf(defaultColor);
        }
        return result;
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
