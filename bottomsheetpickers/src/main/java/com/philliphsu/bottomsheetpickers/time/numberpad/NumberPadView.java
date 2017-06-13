package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.widget.TextView;

public class NumberPadView extends GridPickerView {
    /**
     * Indices map to buttons that represent those numbers.
     * E.g. index 0 -> zero button (located at position 10 in the grid).
     */
    private final TextView[] mNumberButtons = new TextView[10];
    private final TextView[] mAltButtons = new TextView[2];

    public NumberPadView(Context context) {
        this(context, null);
    }

    public NumberPadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Store our own references to the grid's buttons by
        // mapping an index to the button that represents that
        // number.
        mNumberButtons[0] = getButton(10);
        mNumberButtons[0].setText(ButtonTextModel.text(0));
        for (int i = 0; i < mNumberButtons.length - 1; i++) {
            final TextView button = getButton(i);
            button.setText(ButtonTextModel.text(i + 1));
            mNumberButtons[i + 1] = button;
        }

        mAltButtons[0] = getButton(9);
        mAltButtons[1] = getButton(11);
    }

    void setNumberKeysEnabled(int lowerLimitInclusive, int upperLimitExclusive) {
        if (lowerLimitInclusive < 0 || upperLimitExclusive > mNumberButtons.length)
            throw new IndexOutOfBoundsException("Upper limit out of range");

        for (int i = 0; i < mNumberButtons.length; i++) {
            mNumberButtons[i].setEnabled(i >= lowerLimitInclusive && i < upperLimitExclusive);
        }
    }

    void setOnNumberKeyClickListener(OnClickListener l) {
        for (TextView tv : mNumberButtons) {
            tv.setOnClickListener(l);
        }
    }

    void setOnAltKeyClickListener(OnClickListener l) {
        mAltButtons[0].setOnClickListener(l);
        mAltButtons[1].setOnClickListener(l);
    }

    void setLeftAltKeyEnabled(boolean enabled) {
        mAltButtons[0].setEnabled(enabled);
    }

    void setRightAltKeyEnabled(boolean enabled) {
        mAltButtons[1].setEnabled(enabled);
    }

    void setLeftAltKeyText(CharSequence text) {
        mAltButtons[0].setText(text);
    }

    void setRightAltKeyText(CharSequence text) {
        mAltButtons[1].setText(text);
    }

    void setNumberKeysTextColor(ColorStateList colors) {
        for (TextView tv : mNumberButtons) {
            tv.setTextColor(colors);
        }
    }

    void setAltKeysTextColor(ColorStateList colors) {
        for (TextView tv : mAltButtons) {
            tv.setTextColor(colors);
        }
    }
}