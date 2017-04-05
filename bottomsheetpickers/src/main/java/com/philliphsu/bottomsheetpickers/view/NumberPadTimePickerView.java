package com.philliphsu.bottomsheetpickers.view;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;

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
        // TODO: Localize these.
        setTextForPosition(9, DateFormat.is24HourFormat(context) ? ":00" : "AM");

        setTextForPosition(10, String.format("%d", 0));

        // TODO: Localize these.
        setTextForPosition(11, DateFormat.is24HourFormat(context) ? ":30" : "PM");
    }
}
