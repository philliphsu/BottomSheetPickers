package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

/**
 * Dialog to type in a time.
 */
public class NumberPadTimePickerDialog extends AlertDialog
        implements INumberPadTimePicker.DialogView {
    public static final String TAG = NumberPadTimePickerDialog.class.getSimpleName();
    private static final String KEY_DIGITS = "digits";
    // TODO: Why do we need the count?
    private static final String KEY_COUNT = "count";
    // TODO: Rename to KEY_HALF_DAY = "half_day" if the AmPmState annotation is renamed to HalfDay.
    private static final String KEY_AM_PM_STATE = "am_pm_state";

    private final NumberPadTimePicker mTimePicker;
    private final INumberPadTimePicker.DialogPresenter mPresenter;
    private final @Nullable OnTimeSetListener mTimeSetListener;

    @Deprecated // TODO: Delete this when we're done testing! This should not make it into release.
    public NumberPadTimePickerDialog(@NonNull Context context) {
        this(context, DateFormat.is24HourFormat(context));
    }

    public NumberPadTimePickerDialog(@NonNull Context context, boolean is24HourMode) {
        this(context, is24HourMode, null);
    }

    public NumberPadTimePickerDialog(@NonNull Context context, boolean is24HourMode,
                                     @Nullable OnTimeSetListener listener) {
        super(context);
        mTimeSetListener = listener;
        mTimePicker = new NumberPadTimePicker(context);

        // TODO: If this model is needed by other classes, make it a singleton.
        final LocaleModel localeModel = new LocaleModel(context);
        mPresenter = new NumberPadTimePickerDialogPresenter(this, localeModel, is24HourMode);

        final OnBackspaceClickHandler onBackspaceClickHandler
                = new OnBackspaceClickHandler(mPresenter);
        mTimePicker.setOnBackspaceClickListener(onBackspaceClickHandler);
        mTimePicker.setOnBackspaceLongClickListener(onBackspaceClickHandler);

        mTimePicker.setOnNumberKeyClickListener(new OnNumberKeyClickListener(mPresenter));
        mTimePicker.setOnAltKeyClickListener(new OnAltKeyClickListener(mPresenter));

        setView(mTimePicker);

        final OnDialogButtonClickListener onDialogButtonClickListener
                = new OnDialogButtonClickListener(mPresenter);
        setButton(BUTTON_POSITIVE, context.getString(android.R.string.ok),
                onDialogButtonClickListener);
        setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel),
                onDialogButtonClickListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        mPresenter.onCreate(readStateFromBundle(savedInstanceState));
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        Log.d(TAG, "onSaveInstanceState()");
        final Bundle bundle = super.onSaveInstanceState();
        final INumberPadTimePicker.State state = mPresenter.getState();
        bundle.putIntArray(KEY_DIGITS, state.getDigits());
        // TODO: Why do we need the count?
        bundle.putInt(KEY_COUNT, state.getCount());
        bundle.putInt(KEY_AM_PM_STATE, state.getAmPmState());
        return bundle;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        mPresenter.onStop();
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

    @Override
    public void setResult(int hour, int minute) {
        if (mTimeSetListener != null) {
            mTimeSetListener.onTimeSet(mTimePicker, hour, minute);
        }
    }

    @NonNull
    private static INumberPadTimePicker.State readStateFromBundle(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            final int[] digits = savedInstanceState.getIntArray(KEY_DIGITS);
            // TODO: Why do we need the count?
            final int count = savedInstanceState.getInt(KEY_COUNT);
            final @AmPmStates.AmPmState int amPmState = savedInstanceState.getInt(KEY_AM_PM_STATE);
            return new NumberPadTimePickerState(digits, count, amPmState);
        } else {
            return NumberPadTimePickerState.EMPTY;
        }
    }
}
