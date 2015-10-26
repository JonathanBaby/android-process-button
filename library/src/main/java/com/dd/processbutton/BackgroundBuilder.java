package com.dd.processbutton;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.os.Build;
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

    private StateListDrawable setupDrawable(TypedArray attr, int cornerRadius) {
        StateListDrawable stateListDrawable = new StateListDrawable();

        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled},
                createDisabledDrawable(attr, cornerRadius));
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed},
                createPressedDrawable(attr, cornerRadius));
        stateListDrawable.addState(new int[]{android.R.attr.state_focused},
                createPressedDrawable(attr, cornerRadius));
        stateListDrawable.addState(new int[]{android.R.attr.state_selected},
                createPressedDrawable(attr, cornerRadius));
        stateListDrawable.addState(new int[]{},
                createNormalDrawable(attr, cornerRadius));

        return stateListDrawable;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Drawable setupDrawableV21(TypedArray attr, int cornerRadius) {
        StateListDrawable stateListDrawable = new StateListDrawable();

        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled},
                createDisabledDrawable(attr, cornerRadius));
        stateListDrawable.addState(new int[]{},
                createNormalDrawable(attr, cornerRadius));

        int blueDark = getColor(R.color.blue_pressed);
        ColorStateList color = getColor(attr, R.styleable.FlatButton_pb_colorPressed, blueDark);

        return new RippleDrawable(color, stateListDrawable, new ColorDrawable(Color.WHITE));
    }

    private Drawable createNormalDrawable(TypedArray attr, int cornerRadius) {
        GradientDrawable drawableNormal =
                (GradientDrawable) getDrawable(R.drawable.rect_normal).mutate();
        drawableNormal.setCornerRadius(cornerRadius);

        int blueNormal = getColor(R.color.blue_normal);
        setColor(drawableNormal, attr, R.styleable.FlatButton_pb_colorNormal, blueNormal);
        return drawableNormal;
    }

    private Drawable createPressedDrawable(TypedArray attr, int cornerRadius) {
        GradientDrawable drawablePressed =
                (GradientDrawable) getDrawable(R.drawable.rect_pressed).mutate();
        drawablePressed.setCornerRadius(cornerRadius);

        int blueDark = getColor(R.color.blue_pressed);
        setColor(drawablePressed, attr, R.styleable.FlatButton_pb_colorPressed, blueDark);

        return drawablePressed;
    }

    private Drawable createDisabledDrawable(TypedArray attr, int cornerRadius) {
        GradientDrawable drawableDisabled =
                (GradientDrawable) getDrawable(R.drawable.rect_disabled).mutate();
        drawableDisabled.setCornerRadius(cornerRadius);

        int blueDisabled = getColor(R.color.blue_disabled);
        setColor(drawableDisabled, attr, R.styleable.FlatButton_pb_colorPressed, blueDisabled);

        return drawableDisabled;
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
