package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.R;

class NumberPadTimePicker extends LinearLayout implements INumberPadTimePicker.View {

    private final NumberPadTimePickerView mNumberPad;
    private final LinearLayout mHeaderLayout;
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
        mHeaderLayout = (LinearLayout) findViewById(R.id.bsp_input_time_container);
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
        mHeaderLayout = (LinearLayout) findViewById(R.id.bsp_input_time_container);
        mTimeDisplay = (TextView) findViewById(R.id.bsp_input_time);
        mAmPmDisplay = (TextView) findViewById(R.id.bsp_input_ampm);
        mBackspace = findViewById(R.id.bsp_backspace);
    }

    @Override
    public void setNumberKeysEnabled(int start, int end) {
        mNumberPad.setNumberKeysEnabled(start, end);
    }

    @Override
    public void setBackspaceEnabled(boolean enabled) {
        mBackspace.setEnabled(enabled);
    }

    @Override
    public void updateTimeDisplay(CharSequence time) {
        mTimeDisplay.setText(time);
    }

    @Override
    public void updateAmPmDisplay(CharSequence ampm) {
        mAmPmDisplay.setText(ampm);
    }

    @Override
    public void setOkButtonEnabled(boolean enabled) {
        // There is no 'ok' button.
    }

    @Override
    public void setAmPmDisplayVisible(boolean visible) {
        mAmPmDisplay.setVisibility(visible ? VISIBLE : GONE);
    }

    @Override
    public void setAmPmDisplayIndex(int index) {
        if (index == 1) return;
        mHeaderLayout.removeViewAt(1);
        mHeaderLayout.addView(mAmPmDisplay, 0);
    }

    @Override
    public void setIs24HourMode(boolean is24HourMode) {
        mNumberPad.setIs24HourMode(is24HourMode);
    }

    @Override
    public void setLeftAltKeyEnabled(boolean enabled) {
        mNumberPad.setLeftAltKeyEnabled(enabled);
    }

    @Override
    public void setRightAltKeyEnabled(boolean enabled) {
        mNumberPad.setRightAltKeyEnabled(enabled);
    }

    void setOnBackspaceClickListener(OnClickListener l) {
        mBackspace.setOnClickListener(l);
    }

    void setOnBackspaceLongClickListener(OnLongClickListener l) {
        mBackspace.setOnLongClickListener(l);
    }

    void setOnNumberKeyClickListener(OnClickListener l) {
        mNumberPad.setOnNumberKeyClickListener(l);
    }

    void setOnAltKeyClickListener(OnClickListener l) {
        mNumberPad.setOnAltKeyClickListener(l);
    }
}
