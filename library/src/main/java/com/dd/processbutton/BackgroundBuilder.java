package com.dd.processbutton;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;

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
        ColorStateList colorStateList = getMergedColorStateList(attr);
        GradientDrawable roundedRectDrawable = (GradientDrawable) getDrawable(R.drawable.rounded_rect).mutate();

        roundedRectDrawable.setCornerRadius(cornerRadius);
        Drawable drawable = DrawableCompat.wrap(roundedRectDrawable);
        DrawableCompat.setTintList(drawable, colorStateList);
        return drawable;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Drawable setupDrawableV21(TypedArray attr, int cornerRadius) {
        ColorStateList colorStateList = getMergedColorStateList(attr);
        RippleDrawable rippleDrawable = (RippleDrawable) getDrawable(R.drawable.rounded_rect);
        GradientDrawable roundedRectDrawable = (GradientDrawable) rippleDrawable.getDrawable(0);

        roundedRectDrawable.setCornerRadius(cornerRadius);
        roundedRectDrawable.setTintList(colorStateList);
        return rippleDrawable;
    }

    @NonNull
    private ColorStateList getMergedColorStateList(TypedArray attr) {
        return new ColorStateList(
                new int[][]
                        {
                                new int[]{-android.R.attr.state_enabled},
                                new int[]{android.R.attr.state_pressed},
                                new int[]{android.R.attr.state_focused},
                                new int[]{}
                        },
                new int[]
                        {
                                attr.getColor(R.styleable.FlatButton_pb_colorDisabled, getColor(R.color.btn_disabled)),
                                attr.getColor(R.styleable.FlatButton_pb_colorPressed, getColor(R.color.btn_pressed)),
                                attr.getColor(R.styleable.FlatButton_pb_colorFocused, getColor(R.color.btn_focused)),
                                attr.getColor(R.styleable.FlatButton_pb_colorNormal, getColor(R.color.btn_normal))
                        }
        );
    }

    public float getDimension(int id) {
        return context.getResources().getDimension(id);
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
}
