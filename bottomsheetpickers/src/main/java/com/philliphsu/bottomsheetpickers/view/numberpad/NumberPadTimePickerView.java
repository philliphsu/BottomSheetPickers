package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;

import com.philliphsu.bottomsheetpickers.view.DateTimeFormatUtils;
import com.philliphsu.bottomsheetpickers.view.GridPickerView;

import java.text.DateFormatSymbols;

/**
 * Created by Phillip Hsu on 4/4/2017.
 */

class NumberPadTimePickerView extends GridPickerView {

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
        setIs24HourMode(DateFormat.is24HourFormat(context));
    }

    void setNumberKeysEnabled(int start, int end) {
        // TODO
    }

    void setOnNumberKeyClickListener(OnClickListener l) {
        setOnButtonClickListener(l);
    }

    void setIs24HourMode(boolean is24HourMode) {
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

        mIs24HourMode = is24HourMode;
    }
}
