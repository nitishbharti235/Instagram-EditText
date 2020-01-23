package com.example.instaedittext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class InstaEditText extends EditText {

    private int mMaxTextSize = 200;
    private int mMinTextSize = 16;
    private int mWidthPx;

    private TextPaint mPaint;
    private boolean mInitialized = false;

    private boolean mResizing = false;

    private Handler mResizingHandler;

    private final Map<String, Integer> mCachedSizes = new HashMap<>();

    public InstaEditText(Context context) {
        this(context, null);
    }

    public InstaEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("HandlerLeak")
    public InstaEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMaxTextSize = context.getResources().getDimensionPixelSize(R.dimen.maxTextSize);
        mMinTextSize = context.getResources().getDimensionPixelSize(R.dimen.minTextSize);
        mWidthPx = context.getResources().getDimensionPixelSize(R.dimen.autoresize_min_width);

        mResizingHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                adjustTextSize();
            }
        };
    }

    @Override
    public void setTypeface(final Typeface tf) {
        if (mPaint == null) {
            mPaint = new TextPaint(getPaint());
        }

        mPaint.setTypeface(tf);
        super.setTypeface(tf);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        adjustTextSize();
    }

    private void adjustTextSize() {
        int measuredWidth = getMeasuredWidth();
        if (measuredWidth <= 0) return;

        int availableWidth = measuredWidth - getCompoundPaddingLeft() - getCompoundPaddingRight();
        Editable text = getText();
        int selectionEnd = getSelectionEnd();
        String string = text.toString();
        TextPaint textPaint = new TextPaint(mPaint);
        textPaint.setTextSize(mMinTextSize);
        Layout layout = new StaticLayout(
                string,
                textPaint,
                availableWidth,
                Layout.Alignment.ALIGN_CENTER,
                1 , 0 , true );

        int lineCount = layout.getLineCount();

        for (int i=0; i<lineCount; i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            String substring = string.substring(lineStart, lineEnd);

            int setTextSize = binarySearchSize(textPaint, i, substring, availableWidth, mMinTextSize, mMaxTextSize);
            text.setSpan(
                    new AbsoluteSizeSpan(setTextSize),
                    lineStart,
                    lineEnd,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        setSelection(selectionEnd);
    }

    private static int binarySearchSize(TextPaint textPaint, int linePosition, String text, int width, int minSize, int maxSize){
        int lowSize = minSize;
        int highSize = maxSize;
        int currentSize = lowSize + (int)Math.floor((highSize - lowSize) / 2f);
        while (lowSize < currentSize) {
            if (hasLineBreak(textPaint, linePosition, text, currentSize, width)) {
                highSize = currentSize;
            } else {
                lowSize = currentSize;
            }
            currentSize = lowSize + (int)Math.floor((highSize - lowSize) / 2f);
        }
        return currentSize;
    }

//    private Matrix matrix;
//    public void setMatrix(float fl){
//        matrix= new Matrix();
//        matrix.postScale(fl,fl);
//        invalidate();
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        if (matrix != null){
//            canvas.setMatrix(matrix);
//        }
//        super.onDraw(canvas);
//    }

    private static boolean hasLineBreak(TextPaint textPaint, int linePosition, String text, int currentSize, int width) {
        textPaint.setTextSize(currentSize);
        float measureWidth = textPaint.measureText(text);
        return measureWidth > width;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mResizingHandler != null) {
            mResizingHandler.removeMessages(0);
        }
    }
}
