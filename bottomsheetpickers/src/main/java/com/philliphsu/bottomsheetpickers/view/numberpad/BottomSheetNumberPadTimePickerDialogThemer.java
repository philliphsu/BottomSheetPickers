package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Adds {@link NumberPadTimePicker#LAYOUT_BOTTOM_SHEET bottom sheet} theming APIs
 * to the {@link NumberPadTimePickerThemer base set of APIs}.
 */
public class BottomSheetNumberPadTimePickerDialogThemer extends NumberPadTimePickerDialogThemer {

    /** Option to place the backspace button in the header. */
    public static final int LOCATION_HEADER =
            NumberPadTimePickerBottomSheetComponent.LOCATION_HEADER;
    /** Option to place the backspace button in the footer. */
    public static final int LOCATION_FOOTER =
            NumberPadTimePickerBottomSheetComponent.LOCATION_FOOTER;

    @IntDef({LOCATION_HEADER, LOCATION_FOOTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BackspaceLocation {}

    /** Option to always show the FAB. */
    public static final int SHOW_FAB_ALWAYS =
            NumberPadTimePickerBottomSheetComponent.SHOW_FAB_ALWAYS;
    /** Option to only show the FAB when the inputted sequence makes a valid time. */
    public static final int SHOW_FAB_VALID_TIME =
            NumberPadTimePickerBottomSheetComponent.SHOW_FAB_VALID_TIME;

    @IntDef({SHOW_FAB_ALWAYS, SHOW_FAB_VALID_TIME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShowFabPolicy {}

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

    public BottomSheetNumberPadTimePickerDialogThemer setAnimateFabBackgroundColor(boolean animate) {
        mTimePickerComponent.setAnimateFabBackgroundColor(animate);
        return this;
    }

    public BottomSheetNumberPadTimePickerDialogThemer setShowFabPolicy(@ShowFabPolicy int policy) {
        mTimePickerComponent.setShowFabPolicy(policy);
        return this;
    }

    public BottomSheetNumberPadTimePickerDialogThemer setAnimateFabIn(boolean animateIn) {
        mTimePickerComponent.setAnimateFabIn(animateIn);
        return this;
    }

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
