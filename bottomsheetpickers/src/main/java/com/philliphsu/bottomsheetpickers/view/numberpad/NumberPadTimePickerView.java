package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;

import com.philliphsu.bottomsheetpickers.view.DateTimeFormatUtils;
import com.philliphsu.bottomsheetpickers.view.GridPickerView;

/**
 * Created by Phillip Hsu on 4/4/2017.
 */

class NumberPadTimePickerView extends GridPickerView {

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

        final String altText1, altText2;
        if (DateFormat.is24HourFormat(context)) {
            final String timeSeparator = DateTimeFormatUtils.getTimeSeparator(context, true);
            altText1 = timeSeparator + String.format("%02d", 0);
            altText2 = timeSeparator + String.format("%02d", 30);
        } else  {
            // TODO: Get localized.
            altText1 = "AM";
            altText2 = "PM";
        }
        // TODO: Apply a smaller text size.
        setTextForPosition(9, altText1);
        setTextForPosition(10, String.format("%d", 0));
        // TODO: Apply a smaller text size.
        setTextForPosition(11, altText2);
    }

    void setNumberKeysEnabled(int start, int end) {
        // TODO
    }
}
