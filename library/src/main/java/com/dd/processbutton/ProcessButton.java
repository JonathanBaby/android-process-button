package com.dd.processbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

public abstract class ProcessButton extends FlatButton {

    private int mProgress;
    private int mMaxProgress;
    private int mMinProgress;

    private GradientDrawable mProgressDrawable;
    private GradientDrawable mCompleteDrawable;
    private GradientDrawable mErrorDrawable;

    private CharSequence mLoadingText;
    private CharSequence mCompleteText;
    private CharSequence mErrorText;

    private boolean blockClicksWhenLoading;

    private OnClickListener onClickListener;

    private boolean autoResumeToNormalState;
    private boolean autoResumeTaskPosted;
    private int autoResumeToNormalStateDelay = 2000;
    private Runnable autoResumeToNormalStateTask = new Runnable() {
        @Override
        public void run() {
            autoResumeTaskPosted = false;
            if (isErrorState() || isCompleteState()) {
                setProgress(0);
            }
        }
    };

    public ProcessButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public ProcessButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProcessButton(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        mMinProgress = 0;
        mMaxProgress = 100;

        mProgressDrawable = (GradientDrawable) getDrawable(R.drawable.rect_progress).mutate();
        mProgressDrawable.setCornerRadius(getCornerRadius());

        mCompleteDrawable = (GradientDrawable) getDrawable(R.drawable.rect_complete).mutate();
        mCompleteDrawable.setCornerRadius(getCornerRadius());

        mErrorDrawable = (GradientDrawable) getDrawable(R.drawable.rect_error).mutate();
        mErrorDrawable.setCornerRadius(getCornerRadius());

        if (attrs != null) {
            initAttributes(attrs);
        }

        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blockClicksWhenLoading && isProgressState()) {
                    return;
                }

                if (autoResumeToNormalState
                        && (isErrorState() || isCompleteState())) {
                    setProgress(0);
                }

                if (onClickListener != null) {
                    onClickListener.onClick(v);
                }
            }
        });
    }

    private void initAttributes(AttributeSet attributeSet) {
        TypedArray attr = getTypedArray(attributeSet, R.styleable.ProcessButton);

        if (attr == null) {
            return;
        }

        try {
            mLoadingText = attr.getText(R.styleable.ProcessButton_pb_textProgress);
            mCompleteText = attr.getText(R.styleable.ProcessButton_pb_textComplete);
            mErrorText = attr.getText(R.styleable.ProcessButton_pb_textError);
            blockClicksWhenLoading = attr.getBoolean(R.styleable.ProcessButton_pb_blockClicksWhenLoading, blockClicksWhenLoading);
            autoResumeToNormalState = attr.getBoolean(R.styleable.ProcessButton_pb_autoResumeToNormal, autoResumeToNormalState);
            autoResumeToNormalStateDelay = attr.getInt(R.styleable.ProcessButton_pb_autoResumeToNormalDelay, autoResumeToNormalStateDelay);

            int purple = getColor(R.color.btn_progress);
            setColor(mProgressDrawable, attr, R.styleable.ProcessButton_pb_colorProgress, purple);

            int green = getColor(R.color.btn_complete);
            setColor(mCompleteDrawable, attr, R.styleable.ProcessButton_pb_colorComplete, green);

            int red = getColor(R.color.btn_error);
            setColor(mErrorDrawable, attr, R.styleable.ProcessButton_pb_colorError, red);
        } finally {
            attr.recycle();
        }
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setAutoResumeToNormalState(boolean autoResumeToNormalState) {
        this.autoResumeToNormalState = autoResumeToNormalState;
    }

    public void setBlockClicksWhenLoading(boolean blockClicksWhenLoading) {
        this.blockClicksWhenLoading = blockClicksWhenLoading;
    }

    public void setProgress(int progress) {
        mProgress = progress;

        if (isNormalState()) {
            onNormalState();
        } else if (isCompleteState()) {
            onCompleteState();
        } else if (isErrorState()){
            onErrorState();
        } else {
            onProgress();
        }

        invalidate();
    }

    public void setNormalState() {
        setProgress(getMinProgress());
    }

    public void setCompleteState() {
        setProgress(getMaxProgress());
    }

    public void setErrorState() {
        setProgress(getMinProgress() - 1);
    }

    public void setLoadingState() {
        setProgress(getMinProgress() + 1);
    }

    public boolean isErrorState() {
        return mProgress < mMinProgress;
    }

    public boolean isCompleteState() {
        return mProgress >= mMaxProgress;
    }

    public boolean isNormalState() {
        return mProgress == mMinProgress;
    }

    public boolean isProgressState() {
        return mProgress > mMinProgress && mProgress < mMaxProgress;
    }

    protected void onErrorState() {
        if(getErrorText() != null) {
            saveTextIfNotYet();
            setText(getErrorText());
        }
        setBackgroundCompat(getErrorDrawable());
        returnToNormalStateIfNeeded();
    }

    protected void onProgress() {
        if(getLoadingText() != null) {
            saveTextIfNotYet();
            setText(getLoadingText());
        }
        setBackgroundCompat(getNormalDrawable());
        cancelReturnToNormalTask();
    }

    protected void onCompleteState() {
        if(getCompleteText() != null) {
            saveTextIfNotYet();
            setText(getCompleteText());
        }
        setBackgroundCompat(getCompleteDrawable());
        returnToNormalStateIfNeeded();
    }

    protected void onNormalState() {
        restoreText();
        setBackgroundCompat(getNormalDrawable());
        cancelReturnToNormalTask();
    }

    private void returnToNormalStateIfNeeded() {
        if (!autoResumeToNormalState
                || autoResumeTaskPosted) {
            return;
        }

        Handler handler = getHandler();
        if (handler != null) {
            autoResumeTaskPosted = true;
            handler.postDelayed(autoResumeToNormalStateTask, autoResumeToNormalStateDelay);
        }
    }

    private void cancelReturnToNormalTask() {
        autoResumeTaskPosted = false;
        Handler handler = getHandler();
        if (handler != null) {
            handler.removeCallbacks(autoResumeToNormalStateTask);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // progress
        if(mProgress > mMinProgress && mProgress < mMaxProgress) {
            drawProgress(canvas);
        }

        super.onDraw(canvas);
    }

    public abstract void drawProgress(Canvas canvas);

    public int getProgress() {
        return mProgress;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public int getMinProgress() {
        return mMinProgress;
    }

    public GradientDrawable getProgressDrawable() {
        return mProgressDrawable;
    }

    public GradientDrawable getCompleteDrawable() {
        return mCompleteDrawable;
    }

    public CharSequence getLoadingText() {
        return mLoadingText;
    }

    public CharSequence getCompleteText() {
        return mCompleteText;
    }

    public void setProgressDrawable(GradientDrawable progressDrawable) {
        mProgressDrawable = progressDrawable;
    }

    public void setCompleteDrawable(GradientDrawable completeDrawable) {
        mCompleteDrawable = completeDrawable;
    }

    public void setLoadingText(CharSequence loadingText) {
        mLoadingText = loadingText;
    }

    public void setCompleteText(CharSequence completeText) {
        mCompleteText = completeText;
    }

    public GradientDrawable getErrorDrawable() {
        return mErrorDrawable;
    }

    public void setErrorDrawable(GradientDrawable errorDrawable) {
        mErrorDrawable = errorDrawable;
    }

    public CharSequence getErrorText() {
        return mErrorText;
    }

    public void setErrorText(CharSequence errorText) {
        mErrorText = errorText;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.mProgress = mProgress;

        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            super.onRestoreInstanceState(savedState.getSuperState());
            setProgress(savedState.mProgress);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    /**
     * A {@link android.os.Parcelable} representing the {@link com.dd.processbutton.ProcessButton}'s
     * state.
     */
    public static class SavedState extends FlatButton.SavedState {

        private int mProgress;

        public SavedState(Parcelable parcel) {
            super(parcel);
        }

        protected SavedState(Parcel in) {
            super(in);
            mProgress = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mProgress);
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
