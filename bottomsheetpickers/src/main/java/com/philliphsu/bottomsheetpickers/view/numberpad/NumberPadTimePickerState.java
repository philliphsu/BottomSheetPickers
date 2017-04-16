package com.philliphsu.bottomsheetpickers.view.numberpad;

import java.util.Arrays;

/**
 * Immutable representation of the instance state of a number pad time picker.
 */
final class NumberPadTimePickerState implements INumberPadTimePicker.State {
    private final int[] mDigits;
    private final int mCount;

    public NumberPadTimePickerState(int[] digits, int count) {
        mDigits = Arrays.copyOf(digits, digits.length);
        mCount = count;
    }

    @Override
    public int[] getDigits() {
        return Arrays.copyOf(mDigits, mDigits.length);
    }

    @Override
    public int getCount() {
        return mCount;
    }
}
