package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;

/**
 * APIs to set the colors of a {@link NumberPadTimePicker}.
 */
interface NumberPadTimePickerThemer {
    NumberPadTimePickerThemer setInputTimeTextColor(@ColorInt int color);

    NumberPadTimePickerThemer setInputAmPmTextColor(@ColorInt int color);

    NumberPadTimePickerThemer setBackspaceTint(ColorStateList colors);

    NumberPadTimePickerThemer setNumberKeysTextColor(ColorStateList colors);

    NumberPadTimePickerThemer setAltKeysTextColor(ColorStateList colors);

    NumberPadTimePickerThemer setHeaderBackground(Drawable background);

    NumberPadTimePickerThemer setNumberPadBackground(Drawable background);

    NumberPadTimePickerThemer setDivider(Drawable divider);
}
