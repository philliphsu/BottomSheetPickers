package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.view.DateTimeFormatUtils;
import com.philliphsu.bottomsheetpickers.view.GridPickerView;

import java.text.DateFormatSymbols;

/**
 * Created by Phillip Hsu on 4/4/2017.
 */

class NumberPadTimePickerView extends GridPickerView {
    /**
     * Indices map to buttons that represent those numbers.
     * E.g. index 0 -> zero button (located at position 10 in the grid).
     */
    private final TextView[] mNumberButtons = new TextView[10];

    private final TextView[] mAltButtons = new TextView[2];

    private boolean mIs24HourMode;

    public NumberPadTimePickerView(Context context) {
        this(context, null);
    }

    public NumberPadTimePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPadTimePickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        for (int i = 0; i < 9; i++) {
            setTextForPosition(i, String.format("%d", i + 1));
        }
        setTextForPosition(10, String.format("%d", 0));

        final boolean is24HourMode = DateFormat.is24HourFormat(context);
        setAltKeysTexts(is24HourMode);
        mIs24HourMode = is24HourMode;

        // Store our own references to the grid's buttons by
        // mapping an index to the button that represents that
        // number.
        mNumberButtons[0] = getButton(10);
        for (int i = 0; i < mNumberButtons.length - 1; i++) {
            mNumberButtons[i + 1] = getButton(i);
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

    void setIs24HourMode(boolean is24HourMode) {
        if (mIs24HourMode != is24HourMode) {
            setAltKeysTexts(is24HourMode);
            mIs24HourMode = is24HourMode;
        }
    }

    void setLeftAltKeyEnabled(boolean enabled) {
        mAltButtons[0].setEnabled(enabled);
    }

    void setRightAltKeyEnabled(boolean enabled) {
        mAltButtons[1].setEnabled(enabled);
    }

    private boolean is24HourFormat() {
        return mIs24HourMode;
    }

    private void setAltKeysTexts(boolean is24HourMode) {
        final String altText1, altText2;
        if (is24HourMode) {
            final String timeSeparator = DateTimeFormatUtils.getTimeSeparator(getContext(), true);
            altText1 = timeSeparator + String.format("%02d", 0);
            altText2 = timeSeparator + String.format("%02d", 30);
        } else  {
            String[] amPm = new DateFormatSymbols().getAmPmStrings();
            // TODO: Get localized. Or get the same am/pm strings as the framework.
            altText1 = amPm[0].length() > 2 ? "AM" : amPm[0];
            altText2 = amPm[1].length() > 2 ? "PM" : amPm[1];
        }
        // TODO: Apply a smaller text size.
        setTextForPosition(9, altText1);
        // TODO: Apply a smaller text size.
        setTextForPosition(11, altText2);
    }
}