package com.example.instaedittext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.widget.EditText;


public class InstaEditText extends EditText {

    private int maxTextSize = 200;
    private int minTextSize = 16;
    private int widthPx;

    private TextPaint paint;

    private Handler resizingHandler;


    public InstaEditText(Context context) {
        this(context, null);
    }

    public InstaEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("HandlerLeak")
    public InstaEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        maxTextSize = context.getResources().getDimensionPixelSize(R.dimen.maxTextSize);
        minTextSize = context.getResources().getDimensionPixelSize(R.dimen.minTextSize);
        widthPx = context.getResources().getDimensionPixelSize(R.dimen.autoresize_min_width);

        resizingHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                adjustTextSize();
            }
        };
    }

    @Override
    public void setTypeface(final Typeface tf) {
        if (paint == null) {
            paint = new TextPaint(getPaint());
        }

        paint.setTypeface(tf);
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
        TextPaint textPaint = new TextPaint(paint);
        textPaint.setTextSize(minTextSize);
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

            int setTextSize = binarySearchSize(textPaint, i, substring, availableWidth, minTextSize, maxTextSize);
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

    private static boolean hasLineBreak(TextPaint textPaint, int linePosition, String text, int currentSize, int width) {
        textPaint.setTextSize(currentSize);
        float measureWidth = textPaint.measureText(text);
        return measureWidth > width;
    }

}
