package com.dd.processbutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.Button;

import com.dd.processbutton.utils.DrawableUtils;

public class FlatButton extends Button {

    private Drawable mDrawable;
    private float cornerRadius;
    private BackgroundBuilder backgroundBuilder;

    private boolean hasSavedText;
    private String mSavedText;

    public FlatButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public FlatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FlatButton(Context context) {
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

        mSavedText = getText().toString();
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

    protected void saveTextIfNotYet() {
        if (!hasSavedText) {
            hasSavedText = true;
            mSavedText = getText().toString();
        }
    }

    protected void restoreText() {
        if (hasSavedText) {
            setText(mSavedText);

            hasSavedText = false;
            mSavedText = null;
        }
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
        DrawableUtils.setBackgroundCompat(this, drawable);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.savedText = mSavedText;
        savedState.hasSavedText = hasSavedText;

        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            super.onRestoreInstanceState(savedState.getSuperState());
            mSavedText = savedState.savedText;
            hasSavedText = savedState.hasSavedText;
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    /**
     * A {@link android.os.Parcelable} representing the {@link com.dd.processbutton.FlatButton}'s
     * state.
     */
    public static class SavedState extends BaseSavedState {

        private String savedText;
        private boolean hasSavedText;

        public SavedState(Parcelable parcel) {
            super(parcel);
        }

        protected SavedState(Parcel in) {
            super(in);
            savedText = in.readString();
            hasSavedText = in.readInt() > 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(savedText);
            out.writeInt(hasSavedText ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
