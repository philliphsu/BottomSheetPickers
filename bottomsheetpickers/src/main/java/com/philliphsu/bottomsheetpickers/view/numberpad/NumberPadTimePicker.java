package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.R;

// TODO: Declare an attribute with format="reference" to allow a style resource to be specified.
class NumberPadTimePicker extends LinearLayout implements INumberPadTimePicker.View {
    /**
     * Option to layout this view for use in an alert dialog.
     */
    static final int LAYOUT_ALERT = 1;
    /**
     * Option to layout this view for use in a bottom sheet dialog.
     */
    static final int LAYOUT_BOTTOM_SHEET = 2;

    private NumberPadView mNumberPad;
    private LinearLayout mHeaderLayout;
    private TextView mTimeDisplay;
    private TextView mAmPmDisplay;
    private View mBackspace;
    private @Nullable View mOkButton;

    public NumberPadTimePicker(Context context) {
        this(context, null);
    }

    public NumberPadTimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0 /*TODO: Pass our attribute here. This contains a reference to a style resource.*/);
    }

    public NumberPadTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public NumberPadTimePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    // TODO: Apply the style resource, either the one contained in defStyleAttr or defStyleRes itself.
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setOrientation(VERTICAL);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BSP_NumberPadTimePicker, defStyleAttr, defStyleRes);
        final int layout = a.getInt(
                R.styleable.BSP_NumberPadTimePicker_bsp_numberPadTimePickerLayout, LAYOUT_ALERT);
        a.recycle();

        final @LayoutRes int layoutRes = layout == LAYOUT_BOTTOM_SHEET
                ? R.layout.bsp_bottomsheet_numberpad_time_picker
                : R.layout.bsp_numberpad_time_picker;
        inflate(context, layoutRes, this);

        mNumberPad = (NumberPadView) findViewById(R.id.bsp_numberpad_time_picker_view);
        mHeaderLayout = (LinearLayout) findViewById(R.id.bsp_input_time_container);
        mTimeDisplay = (TextView) findViewById(R.id.bsp_input_time);
        mAmPmDisplay = (TextView) findViewById(R.id.bsp_input_ampm);
        mBackspace = findViewById(R.id.bsp_backspace);
        mOkButton = findViewById(R.id.bsp_ok_button);
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
    public void setAmPmDisplayVisible(boolean visible) {
        mAmPmDisplay.setVisibility(visible ? VISIBLE : GONE);
    }

    @Override
    public void setAmPmDisplayIndex(int index) {
        if (index != 0 && index != 1) {
            throw new IllegalArgumentException("Index of AM/PM display must be 0 or 1. index == " + index);
        }
        if (index == 1) return;
        mHeaderLayout.removeViewAt(1);
        mHeaderLayout.addView(mAmPmDisplay, 0);
    }

    @Override
    public void setLeftAltKeyText(CharSequence text) {
        mNumberPad.setLeftAltKeyText(text);
    }

    @Override
    public void setRightAltKeyText(CharSequence text) {
        mNumberPad.setRightAltKeyText(text);
    }

    @Override
    public void setLeftAltKeyEnabled(boolean enabled) {
        mNumberPad.setLeftAltKeyEnabled(enabled);
    }

    @Override
    public void setRightAltKeyEnabled(boolean enabled) {
        mNumberPad.setRightAltKeyEnabled(enabled);
    }

    @Deprecated
    @Override
    public void setHeaderDisplayFocused(boolean focused) {
        // Do nothing.
    }

    @Nullable
    View getOkButton() {
        return mOkButton;
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
