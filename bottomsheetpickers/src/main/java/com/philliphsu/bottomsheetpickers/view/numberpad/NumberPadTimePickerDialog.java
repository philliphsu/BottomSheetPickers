package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;

/**
 * Dialog to type in a time.
 */
public class NumberPadTimePickerDialog extends AlertDialog implements INumberPadTimePicker.View {

    private final NumberPadTimePicker mTimePicker;
    private final INumberPadTimePicker.Presenter mPresenter;

    private final boolean mIs24HourMode;

    @Deprecated // TODO: Delete this when we're done testing! This should not make it into release.
    public NumberPadTimePickerDialog(@NonNull Context context) {
        this(context, DateFormat.is24HourFormat(context));
    }

    public NumberPadTimePickerDialog(@NonNull Context context, boolean is24HourMode) {
        super(context);

        mTimePicker = new NumberPadTimePicker(context);
        mPresenter = new NumberPadTimePickerPresenter(this, is24HourMode);
        mTimePicker.setOnBackspaceClickListener(new OnBackspaceClickListener(mPresenter));
        mTimePicker.setOnNumberKeyClickListener(new OnNumberKeyClickListener(mPresenter));
        setView(mTimePicker);

        mIs24HourMode = is24HourMode;

        setButton(BUTTON_POSITIVE, context.getString(android.R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        setOnShowListener(new OnShowTimePickerListener(mPresenter));
    }

    @Override
    public void setNumberKeysEnabled(int start, int end) {
        mTimePicker.setNumberKeysEnabled(start, end);
    }

    @Override
    public void setBackspaceEnabled(boolean enabled) {
        mTimePicker.setBackspaceEnabled(enabled);
    }

    @Override
    public void updateTimeDisplay(CharSequence time) {
        mTimePicker.updateTimeDisplay(time);
    }

    @Override
    public void updateAmPmDisplay(CharSequence ampm) {
        mTimePicker.updateAmPmDisplay(ampm);
    }

    @Override
    public void setOkButtonEnabled(boolean enabled) {
        getButton(BUTTON_POSITIVE).setEnabled(enabled);
    }

    @Override
    public void setAmPmDisplayVisible(boolean visible) {
        mTimePicker.setAmPmDisplayVisible(visible);
    }

    @Override
    public void setAmPmDisplayIndex(int index) {
        mTimePicker.setAmPmDisplayIndex(index);
    }

    @Override
    public void setIs24HourMode(boolean is24HourMode) {
        mTimePicker.setIs24HourMode(is24HourMode);
    }

    @Override
    public void setLeftAltKeyEnabled(boolean enabled) {
        mTimePicker.setLeftAltKeyEnabled(enabled);
    }

    @Override
    public void setRightAltKeyEnabled(boolean enabled) {
        mTimePicker.setRightAltKeyEnabled(enabled);
    }
}
