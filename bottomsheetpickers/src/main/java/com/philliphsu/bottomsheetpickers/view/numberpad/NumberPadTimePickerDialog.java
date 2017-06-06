package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.philliphsu.bottomsheetpickers.R;

/**
 * Dialog to type in a time.
 */
public class NumberPadTimePickerDialog extends AlertDialog {

    private final NumberPadTimePickerDialogViewDelegate mViewDelegate;
    private final NumberPadTimePickerThemer mThemer;

    public NumberPadTimePickerDialog(@NonNull Context context,
            @Nullable OnTimeSetListener listener, boolean is24HourMode) {
        this(context, 0, listener, is24HourMode);
    }

    public NumberPadTimePickerDialog(@NonNull Context context, @StyleRes int themeResId,
            @Nullable OnTimeSetListener listener, boolean is24HourMode) {
        super(context, resolveDialogTheme(context, themeResId));
        final NumberPadTimePicker timePicker = new NumberPadTimePicker(context);
        mViewDelegate = new NumberPadTimePickerDialogViewDelegate(this, getContext(), timePicker,
                null, /* At this point, the AlertDialog has not installed its action buttons yet.
                It does not do so until super.onCreate() returns. */
                listener, is24HourMode);
        mThemer = new NumberPadTimePickerDialogThemer(timePicker);
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

    public NumberPadTimePickerThemer getThemer() {
        return mThemer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Override the dialog's width if we're running in an eligible layout qualifier.
        try {
            getWindow().setLayout(getContext().getResources().getDimensionPixelSize(
                    R.dimen.bsp_alert_dialog_width), ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (Resources.NotFoundException nfe) {
            // Do nothing.
        }
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

    static int resolveDialogTheme(Context context, int resId) {
        if (resId == 0) {
            final TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.bsp_numberPadTimePickerAlertDialogTheme,
                    outValue, true);
            return outValue.resourceId;
        } else {
            return resId;
        }
    }
}
