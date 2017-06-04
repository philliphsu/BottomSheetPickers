package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import static com.philliphsu.bottomsheetpickers.view.Preconditions.checkNotNull;

/**
 * Interface through which a {@link NumberPadTimePicker} contained in
 * a {@link NumberPadTimePickerDialog} or {@link BottomSheetNumberPadTimePickerDialog}
 * can have its colors customized.
 */
public class NumberPadTimePickerDialogThemer implements NumberPadTimePickerThemer {

    final NumberPadTimePicker mTimePicker;

    NumberPadTimePickerDialogThemer(@NonNull NumberPadTimePicker timePicker) {
        mTimePicker = checkNotNull(timePicker);
    }

    @Override
    public void setInputTimeTextColor(@ColorInt int color) {
        mTimePicker.setInputTimeTextColor(color);
    }

    @Override
    public void setInputAmPmTextColor(@ColorInt int color) {
        mTimePicker.setInputAmPmTextColor(color);
    }

    @Override
    public void setBackspaceTint(ColorStateList colors) {
        mTimePicker.setBackspaceTint(colors);
    }

    @Override
    public void setNumberKeysTextColor(ColorStateList colors) {
        mTimePicker.setNumberKeysTextColor(colors);
    }

    @Override
    public void setAltKeysTextColor(ColorStateList colors) {
        mTimePicker.setAltKeysTextColor(colors);
    }

    @Override
    public void setHeaderBackground(Drawable background) {
        mTimePicker.setHeaderBackground(background);
    }

    @Override
    public void setNumberPadBackground(Drawable background) {
        mTimePicker.setNumberPadBackground(background);
    }

    @Override
    public void setDivider(Drawable divider) {
        mTimePicker.setDivider(divider);
    }
}
