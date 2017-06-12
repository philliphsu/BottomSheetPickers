package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TimePicker;

import static com.philliphsu.bottomsheetpickers.time.numberpad.Preconditions.checkNotNull;

/**
 * Handles the {@link INumberPadTimePicker.DialogView DialogView} responsibilities of a number pad time picker dialog.
 */
final class NumberPadTimePickerDialogViewDelegate implements INumberPadTimePicker.DialogView {
    private static final String KEY_DIGITS = "digits";
    // TODO: Why do we need the count?
    private static final String KEY_COUNT = "count";
    private static final String KEY_AM_PM_STATE = "am_pm_state";

    private final @NonNull DialogInterface mDelegator;
    private final @NonNull NumberPadTimePicker mTimePicker;
    private final @Nullable OnTimeSetListener mTimeSetListener;
    private final INumberPadTimePicker.DialogPresenter mPresenter;
    // Dummy TimePicker Passed to onTimeSet() callback.
    private final TimePicker mDummy;

    private View mOkButton;

    // TODO: Consider removing the okButton param because (1) the alert layout does not have it ready
    // at the time of construction and (2) the bottom sheet layout does not need this class anymore
    // to control its FAB. Keep the setOkButton() instead.
    NumberPadTimePickerDialogViewDelegate(@NonNull DialogInterface delegator,
            @NonNull Context context, @NonNull NumberPadTimePicker timePicker,
            @Nullable View okButton, @Nullable OnTimeSetListener listener, boolean is24HourMode) {
        mDelegator = checkNotNull(delegator);
        mTimePicker = checkNotNull(timePicker);
        mOkButton = okButton;
        mTimeSetListener = listener;
        mDummy = new TimePicker(context);

        // TODO: If this model is needed by other classes, make it a singleton.
        final LocaleModel localeModel = new LocaleModel(context);
        mPresenter = new NumberPadTimePickerDialogPresenter(this, localeModel, is24HourMode);

        final OnBackspaceClickHandler backspaceClickHandler = new OnBackspaceClickHandler(mPresenter);
        mTimePicker.setOnBackspaceClickListener(backspaceClickHandler);
        mTimePicker.setOnBackspaceLongClickListener(backspaceClickHandler);
        mTimePicker.setOnNumberKeyClickListener(new OnNumberKeyClickListener(mPresenter));
        mTimePicker.setOnAltKeyClickListener(new OnAltKeyClickListener(mPresenter));
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
        if (mTimePicker.getLayout() == NumberPadTimePicker.LAYOUT_BOTTOM_SHEET) {
            ((NumberPadTimePickerBottomSheetComponent) mTimePicker.getComponent())
                    .setOkButtonEnabled(enabled);
        } else {
            mOkButton.setEnabled(enabled);
        }
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
            mTimeSetListener.onTimeSet(mDummy, hour, minute);
        }
    }

    @Override
    public void cancel() {
        mDelegator.cancel();
    }

    @Override
    public void showOkButton() {
        if (mTimePicker.getLayout() == NumberPadTimePicker.LAYOUT_BOTTOM_SHEET) {
            ((NumberPadTimePickerBottomSheetComponent) mTimePicker.getComponent()).showOkButton();
        }
    }

    void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter.onCreate(readStateFromBundle(savedInstanceState));
    }

    @NonNull
    Bundle onSaveInstanceState(@NonNull Bundle bundle) {
        final INumberPadTimePicker.State state = mPresenter.getState();
        bundle.putIntArray(KEY_DIGITS, state.getDigits());
        // TODO: Why do we need the count?
        bundle.putInt(KEY_COUNT, state.getCount());
        bundle.putInt(KEY_AM_PM_STATE, state.getAmPmState());
        return bundle;
    }

    void onStop() {
        mPresenter.onStop();
    }

    INumberPadTimePicker.DialogPresenter getPresenter() {
        return mPresenter;
    }

    /**
     * Workaround for situations when the 'ok' button is not
     * guaranteed to be available at the time of construction.
     * <p>
     * e.g. {@code AlertDialog}
     */
    void setOkButton(@NonNull View okButton) {
        mOkButton = checkNotNull(okButton);
    }

    @NonNull
    private static INumberPadTimePicker.State readStateFromBundle(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            final int[] digits = savedInstanceState.getIntArray(KEY_DIGITS);
            // TODO: Why do we need the count?
            final int count = savedInstanceState.getInt(KEY_COUNT);
            final @AmPmState int amPmState = savedInstanceState.getInt(KEY_AM_PM_STATE);
            return new NumberPadTimePickerState(digits, count, amPmState);
        } else {
            return NumberPadTimePickerState.EMPTY;
        }
    }
}
