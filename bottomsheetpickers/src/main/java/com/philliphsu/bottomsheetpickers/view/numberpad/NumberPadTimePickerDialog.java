package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

/**
 * Dialog to type in a time.
 */
public class NumberPadTimePickerDialog extends AlertDialog implements INumberPadTimePicker.View {
    private static final String KEY_DIGITS = "digits";
    // TODO: Why do we need the count?
    private static final String KEY_COUNT = "count";

    private final NumberPadTimePicker mTimePicker;
    private final INumberPadTimePicker.Presenter mPresenter;

    @Deprecated // TODO: Delete this when we're done testing! This should not make it into release.
    public NumberPadTimePickerDialog(@NonNull Context context) {
        this(context, DateFormat.is24HourFormat(context));
    }

    public NumberPadTimePickerDialog(@NonNull Context context, boolean is24HourMode) {
        super(context);
        mTimePicker = new NumberPadTimePicker(context);

        // TODO: If this model is needed by other classes, make it a singleton.
        final LocaleModel localeModel = new LocaleModel(context);
        mPresenter = new NumberPadTimePickerPresenter(this, localeModel, is24HourMode);

        final OnBackspaceClickHandler onBackspaceClickHandler
                = new OnBackspaceClickHandler(mPresenter);
        mTimePicker.setOnBackspaceClickListener(onBackspaceClickHandler);
        mTimePicker.setOnBackspaceLongClickListener(onBackspaceClickHandler);

        mTimePicker.setOnNumberKeyClickListener(new OnNumberKeyClickListener(mPresenter));
        mTimePicker.setOnAltKeyClickListener(new OnAltKeyClickListener(mPresenter));

        setView(mTimePicker);
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
    public void setLeftAltKeyText(CharSequence text) {
        mTimePicker.setLeftAltKeyText(text);
    }

    @Override
    public void setRightAltKeyText(CharSequence text) {
        mTimePicker.setRightAltKeyText(text);
    }

    @Override
    public void setLeftAltKeyEnabled(boolean enabled) {
        mTimePicker.setLeftAltKeyEnabled(enabled);
    }

    @Override
    public void setRightAltKeyEnabled(boolean enabled) {
        mTimePicker.setRightAltKeyEnabled(enabled);
    }

    @Override
    public void setHeaderDisplayFocused(boolean focused) {
        mTimePicker.setHeaderDisplayFocused(focused);
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        final Bundle bundle = super.onSaveInstanceState();
        final INumberPadTimePicker.State state = mPresenter.getState();
        bundle.putIntArray(KEY_DIGITS, state.getDigits());
        // TODO: Why do we need the count?
        bundle.putInt(KEY_COUNT, state.getCount());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] digits = savedInstanceState.getIntArray(KEY_DIGITS);
        // TODO: Why do we need the count?
        final int count = savedInstanceState.getInt(KEY_COUNT);
        mPresenter.onRestoreInstanceState(new NumberPadTimePickerState(digits, count));
    }
}
