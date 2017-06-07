package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import static com.philliphsu.bottomsheetpickers.view.numberpad.Preconditions.checkNotNull;

/**
 * Interface through which a {@link NumberPadTimePicker} contained in
 * a {@link NumberPadTimePickerDialog} or {@link BottomSheetNumberPadTimePickerDialog}
 * can have its colors customized.
 */
public class NumberPadTimePickerDialogThemer implements NumberPadTimePickerThemer {

    private final NumberPadTimePicker mTimePicker;

    NumberPadTimePickerDialogThemer(@NonNull NumberPadTimePicker timePicker) {
        mTimePicker = checkNotNull(timePicker);
    }

    @Override
    public NumberPadTimePickerDialogThemer setInputTimeTextColor(@ColorInt int color) {
        mTimePicker.setInputTimeTextColor(color);
        return this;
    }

    @Override
    public NumberPadTimePickerDialogThemer setInputAmPmTextColor(@ColorInt int color) {
        mTimePicker.setInputAmPmTextColor(color);
        return this;
    }

    @Override
    public NumberPadTimePickerDialogThemer setBackspaceTint(ColorStateList colors) {
        mTimePicker.setBackspaceTint(colors);
        return this;
    }

    @Override
    public NumberPadTimePickerDialogThemer setNumberKeysTextColor(ColorStateList colors) {
        mTimePicker.setNumberKeysTextColor(colors);
        return this;
    }

    @Override
    public NumberPadTimePickerDialogThemer setAltKeysTextColor(ColorStateList colors) {
        mTimePicker.setAltKeysTextColor(colors);
        return this;
    }

    @Override
    public NumberPadTimePickerDialogThemer setHeaderBackground(Drawable background) {
        mTimePicker.setHeaderBackground(background);
        return this;
    }

    @Override
    public NumberPadTimePickerDialogThemer setNumberPadBackground(Drawable background) {
        mTimePicker.setNumberPadBackground(background);
        return this;
    }

    @Override
    public NumberPadTimePickerDialogThemer setDivider(Drawable divider) {
        mTimePicker.setDivider(divider);
        return this;
    }
}
