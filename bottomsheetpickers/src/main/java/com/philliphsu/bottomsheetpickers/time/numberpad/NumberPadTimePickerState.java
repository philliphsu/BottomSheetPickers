package com.philliphsu.bottomsheetpickers.time.numberpad;

import java.util.Arrays;

/**
 * Immutable representation of the instance state of a number pad time picker.
 */
final class NumberPadTimePickerState implements INumberPadTimePicker.State {

    static final NumberPadTimePickerState EMPTY = new NumberPadTimePickerState(
            new int[0], 0, AmPmState.UNSPECIFIED);

    private final int[] mDigits;
    private final int mCount;
    private final @AmPmState int mAmPmState;

    public NumberPadTimePickerState(int[] digits, int count, @AmPmState int amPmState) {
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

    @AmPmState
    @Override
    public int getAmPmState() {
        return mAmPmState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumberPadTimePickerState that = (NumberPadTimePickerState) o;

        if (mCount != that.mCount) return false;
        if (mAmPmState != that.mAmPmState) return false;
        return Arrays.equals(mDigits, that.mDigits);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(mDigits);
        result = 31 * result + mCount;
        result = 31 * result + mAmPmState;
        return result;
    }
}
