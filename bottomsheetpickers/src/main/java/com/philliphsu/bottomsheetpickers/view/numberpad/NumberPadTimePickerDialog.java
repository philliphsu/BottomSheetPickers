package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

/**
 * Dialog to type in a time.
 */
public class NumberPadTimePickerDialog extends AlertDialog {

    private final NumberPadTimePickerDialogViewDelegate mViewDelegate;

    public NumberPadTimePickerDialog(@NonNull Context context, @Nullable OnTimeSetListener listener,
                                     boolean is24HourMode) {
        super(context);
        final NumberPadTimePicker timePicker = new NumberPadTimePicker(context);
        mViewDelegate = new NumberPadTimePickerDialogViewDelegate(this, getContext(), timePicker,
                null, /* At this point, the AlertDialog has not installed its action buttons yet.
                It does not do so until super.onCreate() returns. */
                listener, is24HourMode);
        setView(timePicker);

        final OnDialogButtonClickListener onDialogButtonClickListener
                = new OnDialogButtonClickListener(mViewDelegate.getPresenter());
        // If we haven't set these by the time super.onCreate() returns, then the area
        // where the action buttons would normally be is set to GONE visibility.
        setButton(BUTTON_POSITIVE, getContext().getString(android.R.string.ok),
                onDialogButtonClickListener);
        setButton(BUTTON_NEGATIVE, getContext().getString(android.R.string.cancel),
                onDialogButtonClickListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewDelegate.setOkButton(getButton(BUTTON_POSITIVE));
        mViewDelegate.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        return mViewDelegate.onSaveInstanceState(super.onSaveInstanceState());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewDelegate.onStop();
    }
}
