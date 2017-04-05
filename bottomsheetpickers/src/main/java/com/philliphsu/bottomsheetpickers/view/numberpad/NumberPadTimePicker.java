package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.R;

/**
 * Created by Phillip Hsu on 4/5/2017.
 */

public class NumberPadTimePicker extends LinearLayout {

    private final NumberPadTimePickerView mNumberPad;
    private final TextView mTimeDisplay;
    private final TextView mAmPmDisplay;
    private final View mBackspace;

    public NumberPadTimePicker(Context context) {
        this(context, null);
    }

    public NumberPadTimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPadTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        inflate(context, R.layout.bsp_numberpad_time_picker, this);
        mNumberPad = (NumberPadTimePickerView) findViewById(R.id.bsp_numberpad_time_picker_view);
        mTimeDisplay = (TextView) findViewById(R.id.bsp_input_time);
        mAmPmDisplay = (TextView) findViewById(R.id.bsp_input_ampm);
        mBackspace = findViewById(R.id.bsp_backspace);
    }

    @TargetApi(21)
    public NumberPadTimePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(VERTICAL);
        inflate(context, R.layout.bsp_numberpad_time_picker, this);
        mNumberPad = (NumberPadTimePickerView) findViewById(R.id.bsp_numberpad_time_picker_view);
        mTimeDisplay = (TextView) findViewById(R.id.bsp_input_time);
        mAmPmDisplay = (TextView) findViewById(R.id.bsp_input_ampm);
        mBackspace = findViewById(R.id.bsp_backspace);
    }
}
