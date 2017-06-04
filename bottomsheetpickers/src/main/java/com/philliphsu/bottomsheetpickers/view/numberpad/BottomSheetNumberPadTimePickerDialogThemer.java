package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

/**
 * Adds {@link NumberPadTimePicker#LAYOUT_BOTTOM_SHEET bottom sheet} theming APIs
 * to the {@link NumberPadTimePickerThemer base set of APIs}.
 */
public class BottomSheetNumberPadTimePickerDialogThemer extends NumberPadTimePickerDialogThemer {

    BottomSheetNumberPadTimePickerDialogThemer(@NonNull NumberPadTimePicker timePicker) {
        super(timePicker);
    }

    public BottomSheetNumberPadTimePickerDialogThemer setFabBackgroundColor(ColorStateList colors) {
        ((NumberPadTimePickerBottomSheetComponent) mTimePicker.getComponent())
                .setFabBackgroundColor(colors);
        return this;
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setInputTimeTextColor(@ColorInt int color) {
        return (BottomSheetNumberPadTimePickerDialogThemer) super.setInputTimeTextColor(color);
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setInputAmPmTextColor(@ColorInt int color) {
        return (BottomSheetNumberPadTimePickerDialogThemer) super.setInputAmPmTextColor(color);
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setBackspaceTint(ColorStateList colors) {
        return (BottomSheetNumberPadTimePickerDialogThemer) super.setBackspaceTint(colors);
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setNumberKeysTextColor(ColorStateList colors) {
        return (BottomSheetNumberPadTimePickerDialogThemer) super.setNumberKeysTextColor(colors);
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setAltKeysTextColor(ColorStateList colors) {
        return (BottomSheetNumberPadTimePickerDialogThemer) super.setAltKeysTextColor(colors);
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setHeaderBackground(Drawable background) {
        return (BottomSheetNumberPadTimePickerDialogThemer) super.setHeaderBackground(background);
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setNumberPadBackground(Drawable background) {
        return (BottomSheetNumberPadTimePickerDialogThemer) super.setNumberPadBackground(background);
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setDivider(Drawable divider) {
        return (BottomSheetNumberPadTimePickerDialogThemer) super.setDivider(divider);
    }
}
