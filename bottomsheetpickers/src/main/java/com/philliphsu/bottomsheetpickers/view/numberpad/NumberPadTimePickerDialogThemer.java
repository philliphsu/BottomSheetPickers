package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.philliphsu.bottomsheetpickers.view.numberpad.NumberPadTimePicker.NumberPadTimePickerComponent;

import static com.philliphsu.bottomsheetpickers.view.numberpad.Preconditions.checkNotNull;

/**
 * Interface through which a {@link NumberPadTimePicker} contained in
 * a {@link NumberPadTimePickerDialog} or {@link BottomSheetNumberPadTimePickerDialog}
 * can have its colors customized.
 */
public class NumberPadTimePickerDialogThemer implements NumberPadTimePickerThemer {
    private final NumberPadTimePickerComponent mTimePickerComponent;

    NumberPadTimePickerDialogThemer(@NonNull NumberPadTimePickerComponent timePickerComponent) {
        mTimePickerComponent = checkNotNull(timePickerComponent);
    }

    @Override
    public NumberPadTimePickerDialogThemer setInputTimeTextColor(@ColorInt int color) {
        mTimePickerComponent.setInputTimeTextColor(color);
        return this;
    }

    @Override
    public NumberPadTimePickerDialogThemer setInputAmPmTextColor(@ColorInt int color) {
        mTimePickerComponent.setInputAmPmTextColor(color);
        return this;
    }

    @Override
    public NumberPadTimePickerDialogThemer setBackspaceTint(ColorStateList colors) {
        mTimePickerComponent.setBackspaceTint(colors);
        return this;
    }

    @Override
    public NumberPadTimePickerDialogThemer setNumberKeysTextColor(ColorStateList colors) {
        mTimePickerComponent.setNumberKeysTextColor(colors);
        return this;
    }

    @Override
    public NumberPadTimePickerDialogThemer setAltKeysTextColor(ColorStateList colors) {
        mTimePickerComponent.setAltKeysTextColor(colors);
        return this;
    }

    @Override
    public NumberPadTimePickerDialogThemer setHeaderBackground(Drawable background) {
        mTimePickerComponent.setHeaderBackground(background);
        return this;
    }

    @Override
    public NumberPadTimePickerDialogThemer setNumberPadBackground(Drawable background) {
        mTimePickerComponent.setNumberPadBackground(background);
        return this;
    }

    @Override
    public NumberPadTimePickerDialogThemer setDivider(Drawable divider) {
        mTimePickerComponent.setDivider(divider);
        return this;
    }
}
