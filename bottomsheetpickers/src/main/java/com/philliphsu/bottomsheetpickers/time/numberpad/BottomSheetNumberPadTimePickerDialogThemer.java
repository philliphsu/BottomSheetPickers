package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

/**
 * Interface through which a {@link NumberPadTimePicker} contained in
 * {@link BottomSheetNumberPadTimePickerDialog} can have its colors
 * and other attributes customized.
 */
class BottomSheetNumberPadTimePickerDialogThemer extends NumberPadTimePickerDialogThemer
        implements BottomSheetNumberPadTimePickerThemer {
    private final NumberPadTimePickerBottomSheetComponent mTimePickerComponent;

    BottomSheetNumberPadTimePickerDialogThemer(@NonNull NumberPadTimePickerBottomSheetComponent timePickerComponent) {
        super(timePickerComponent);
        mTimePickerComponent = timePickerComponent;
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setFabBackgroundColor(ColorStateList colors) {
        mTimePickerComponent.setFabBackgroundColor(colors);
        return this;
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setFabRippleColor(@ColorInt int color) {
        mTimePickerComponent.setFabRippleColor(color);
        return this;
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setFabIconTint(ColorStateList tint) {
        mTimePickerComponent.setFabIconTint(tint);
        return this;
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setAnimateFabBackgroundColor(boolean animate) {
        mTimePickerComponent.setAnimateFabBackgroundColor(animate);
        return this;
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setShowFabPolicy(@ShowFabPolicy int policy) {
        mTimePickerComponent.setShowFabPolicy(policy);
        return this;
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setAnimateFabIn(boolean animateIn) {
        mTimePickerComponent.setAnimateFabIn(animateIn);
        return this;
    }

    @Override
    public BottomSheetNumberPadTimePickerDialogThemer setBackspaceLocation(@BackspaceLocation int location) {
        mTimePickerComponent.setBackspaceLocation(location);
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
