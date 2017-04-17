package com.philliphsu.bottomsheetpickers.view.numberpad;

import java.util.Arrays;

/**
 * Immutable representation of the instance state of a number pad time picker.
 */
final class NumberPadTimePickerState implements INumberPadTimePicker.State {
    private final int[] mDigits;
    private final int mCount;
    // TODO: If annotation is renamed HalfDay, rename field to mHalfDay.
    private final @AmPmStates.AmPmState int mAmPmState;

    public NumberPadTimePickerState(int[] digits, int count, @AmPmStates.AmPmState int amPmState) {
        mDigits = digits;
        mCount = count;
        mAmPmState = amPmState;
    }

    @Override
    public int[] getDigits() {
        return Arrays.copyOf(mDigits, mDigits.length);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @AmPmStates.AmPmState
    @Override
    public int getAmPmState() {
        return mAmPmState;
    }
}
