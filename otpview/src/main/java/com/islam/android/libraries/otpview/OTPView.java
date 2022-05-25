package com.islam.android.libraries.otpview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

/**
 * OTP View.
 */
public class OTPView extends AppCompatEditText {

    // attrs
    private int digitTextColor;
    private String digitPlaceholder;
    private int digitPlaceholderColor;
    private int digitsNumber;
    private int digitWidth;
    private int digitHeight;
    private int digitPadding;
    private int digitStyle;
    private Drawable digitDrawable;
    private int digitStrokeWidth;
    private int digitStrokeColor;
    private int digitFilledStrokeColor;

    private OnCodeCompleteListener onCodeCompleteListener;
    private Rect textRect;
    private Rect drawableRect;
    private RectF digitRect;
    private Paint digitPaint;


    public OTPView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public OTPView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        setLayoutDirection(LAYOUT_DIRECTION_LTR);
        setInputType(InputType.TYPE_CLASS_NUMBER);
        textRect = new Rect();
        drawableRect = new Rect();
        digitRect = new RectF();
        digitPaint = new Paint();

        // init attrs
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.OTPView);
        try {
            digitTextColor = typedArray.getInteger(R.styleable.OTPView_digitTextColor, Color.BLACK);
            digitPlaceholder = typedArray.getString(R.styleable.OTPView_digitPlaceholder);
            if (digitPlaceholder == null || digitPlaceholder.length() > 1) {
                digitPlaceholder = "_";
            }
            digitPlaceholderColor = typedArray.getInteger(R.styleable.OTPView_digitPlaceholderColor, Color.GRAY);
            digitsNumber = typedArray.getInteger(R.styleable.OTPView_digitsNumber, 6);
            digitWidth = typedArray.getDimensionPixelSize(R.styleable.OTPView_digitWidth, (int) getResources().getDimension(R.dimen.digit_default_width));
            digitHeight = typedArray.getDimensionPixelSize(R.styleable.OTPView_digitHeight, (int) getResources().getDimension(R.dimen.digit_default_height));
            digitPadding = typedArray.getDimensionPixelSize(R.styleable.OTPView_digitPadding, (int) getResources().getDimension(R.dimen.digit_default_padding));

            TypedValue typedValue = new TypedValue();
            boolean isValueExist = typedArray.getValue(R.styleable.OTPView_digitStyle, typedValue);
            digitDrawable = typedArray.getDrawable(R.styleable.OTPView_digitDrawable);
            if (!isValueExist || (typedValue.data == 0 && digitDrawable == null)) {
                digitStyle = 1;
            } else {
                digitStyle = typedValue.data;
            }

            digitStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.OTPView_digitStrokeWidth, (int) getResources().getDimension(R.dimen.digit_default_stroke_width));
            digitStrokeColor = typedArray.getInteger(R.styleable.OTPView_digitStrokeColor, Color.BLACK);
            digitFilledStrokeColor = typedArray.getInteger(R.styleable.OTPView_digitFilledStrokeColor, digitStrokeColor);


        } finally {
            typedArray.recycle();
        }

        // Disable copy paste
        super.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

        // Set max length
        setMaxLength(digitsNumber);
        // Disable long click listener
        setLongClickable(false);
        // Remove background color
        setBackgroundColor(Color.TRANSPARENT);
        // Make cursor Invisible
        setCursorVisible(false);

    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        return false;
    }

    private void setMaxLength(int maxLength) {
        if (maxLength >= 0) {
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        } else {
            setFilters(new InputFilter[0]);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Get width and height information
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // Set recommend height
        if (height < digitHeight) {
            height = digitHeight;
        }

        // Set recommend width
        int recommendWidth = (digitWidth * digitsNumber) + (digitPadding * (digitsNumber - 1));
        if (width < recommendWidth) {
            width = recommendWidth;
        }

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, widthMode);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, heightMode);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setTextColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        setTextColor(digitTextColor);

        // Draw digit
        drawDigit(canvas);
        // Draw text
        drawText(canvas);
    }

    private void drawDigit(Canvas canvas) {

        switch (digitStyle) {
            case 0: {
                drawDrawableDigit(canvas);
                break;
            }
            case 1: {
                drawRectDigit(canvas);
                break;
            }
            case 2: {
                drawLineDigit(canvas);
                break;
            }
        }
    }

    private void drawDrawableDigit(Canvas canvas) {
        // Set digit width and height
        drawableRect.left = 0;
        drawableRect.top = 0;
        drawableRect.right = digitWidth;
        drawableRect.bottom = digitHeight;

        int count = canvas.save();
        digitDrawable.setBounds(drawableRect);
        digitDrawable.setState(new int[]{android.R.attr.state_enabled});
        for (int i = 0; i < digitsNumber; i++) {
            digitDrawable.draw(canvas);
            float dx = digitWidth + digitPadding;
            canvas.translate(dx, 0);
        }
        canvas.restoreToCount(count);
    }

    private void drawRectDigit(Canvas canvas) {
        // Set digit width and height with digit stroke width
        digitRect.left = digitStrokeWidth / 2f;
        digitRect.top = digitStrokeWidth / 2f;
        digitRect.right = digitWidth - (digitStrokeWidth / 2f);
        digitRect.bottom = digitHeight - (digitStrokeWidth / 2f);

        digitPaint.setStyle(Paint.Style.STROKE);
        digitPaint.setStrokeWidth(digitStrokeWidth);
        digitPaint.setAntiAlias(true);

        int count = canvas.save();
        int activatedIndex = Math.max(0, getEditableText().length());
        for (int i = 0; i < digitsNumber; i++) {

            if (i < activatedIndex) {
                digitPaint.setColor(digitFilledStrokeColor);
            } else {
                digitPaint.setColor(digitStrokeColor);
            }
            canvas.drawRoundRect(digitRect, 10, 10, digitPaint);
            float dx = digitWidth + digitPadding;
            canvas.translate(dx, 0);
        }
        canvas.restoreToCount(count);
    }

    private void drawLineDigit(Canvas canvas) {

        digitPaint.setStrokeWidth(digitStrokeWidth);

        int count = canvas.save();
        int activatedIndex = Math.max(0, getEditableText().length());
        for (int i = 0; i < digitsNumber; i++) {
            if (i < activatedIndex) {
                digitPaint.setColor(digitFilledStrokeColor);
            } else {
                digitPaint.setColor(digitStrokeColor);
            }
            canvas.drawLine((digitWidth + digitPadding) * i,
                    digitHeight - digitStrokeWidth / 2f,
                    ((digitWidth + digitPadding) * i) + digitWidth,
                    digitHeight - digitStrokeWidth / 2f,
                    digitPaint
            );
        }
        canvas.restoreToCount(count);
    }

    private void drawText(Canvas canvas) {

        textRect.left = 0;
        textRect.top = 0;
        textRect.right = digitWidth;
        textRect.bottom = digitHeight;

        int count = canvas.getSaveCount();
        int editableTextLength = getEditableText().length();
        for (int i = 0; i < digitsNumber; i++) {

            TextPaint textPaint = getPaint();

            if (i < editableTextLength) {
                String text = String.valueOf(getEditableText().charAt(i));
                textPaint.setColor(digitTextColor);
                // Get text size
                textPaint.getTextBounds(text, 0, 1, textRect);
                // Calculate x and y coordinates
                int x = digitWidth / 2 + (digitWidth + digitPadding) * i - (textRect.centerX());
                int y = digitHeight / 2 - textRect.centerY();
                canvas.drawText(text, x, y, textPaint);
            } else {
                textPaint.setColor(digitPlaceholderColor);
                // Get text size
                textPaint.getTextBounds(digitPlaceholder, 0, 1, textRect);
                // Calculate x and y coordinates
                int x = digitWidth / 2 + (digitWidth + digitPadding) * i - (textRect.centerX());
                int y = digitHeight / 2 - textRect.centerY();
                canvas.drawText(digitPlaceholder, x, y, textPaint);
            }
        }
        canvas.restoreToCount(count);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (getEditableText().length() == digitsNumber && onCodeCompleteListener != null) {
            onCodeCompleteListener.onCodeComplete(getEditableText().toString());
        }
    }

    public void setOnCodeCompleteListener(OnCodeCompleteListener onCodeCompleteListener) {
        this.onCodeCompleteListener = onCodeCompleteListener;
    }

    public interface OnCodeCompleteListener {
        void onCodeComplete(String code);
    }
}