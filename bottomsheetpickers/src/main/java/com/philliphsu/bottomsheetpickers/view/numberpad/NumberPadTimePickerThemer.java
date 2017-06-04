package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;

/**
 * APIs to set the colors of a {@link NumberPadTimePicker}.
 */
interface NumberPadTimePickerThemer {
    void setInputTimeTextColor(@ColorInt int color);

    void setInputAmPmTextColor(@ColorInt int color);

    void setBackspaceTint(ColorStateList colors);

    void setNumberKeysTextColor(ColorStateList colors);

    void setAltKeysTextColor(ColorStateList colors);

    void setHeaderBackground(Drawable background);

    void setNumberPadBackground(Drawable background);

    void setDivider(Drawable divider);
}
