package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

/**
 * Adds {@link NumberPadTimePicker#LAYOUT_BOTTOM_SHEET bottom sheet} theming APIs
 * to the {@link NumberPadTimePickerThemer base set of APIs}.
 */
// TODO: Create setters for the remaining bottom sheet attributes?
public class BottomSheetNumberPadTimePickerDialogThemer extends NumberPadTimePickerDialogThemer {

    private final NumberPadTimePickerBottomSheetComponent mTimePickerComponent;

    BottomSheetNumberPadTimePickerDialogThemer(@NonNull NumberPadTimePicker timePicker) {
        super(timePicker);
        mTimePickerComponent = (NumberPadTimePickerBottomSheetComponent) timePicker.getComponent();
    }

    public BottomSheetNumberPadTimePickerDialogThemer setFabBackgroundColor(ColorStateList colors) {
        mTimePickerComponent.setFabBackgroundColor(colors);
        return this;
    }

    public BottomSheetNumberPadTimePickerDialogThemer setFabRippleColor(@ColorInt int color) {
        mTimePickerComponent.setFabRippleColor(color);
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
