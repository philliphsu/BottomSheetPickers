/*
 * Copyright (C) 2016 Phillip Hsu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.philliphsu.bottomsheetpickers.view.Preconditions;
import com.philliphsu.bottomsheetpickers.view.numberpad.BottomSheetNumberPadTimePickerDialog;
import com.philliphsu.bottomsheetpickers.view.numberpad.BottomSheetNumberPadTimePickerDialogThemer;

/**
 * Dialog to type in a time.
 */
public class NumberPadTimePickerDialog extends BottomSheetTimePickerDialog
        implements TimePickerDialog.OnTimeSetListener {

    private BottomSheetNumberPadTimePickerDialog mDialog;
    private OnTimeSetListener mTimeSetListener;
    private TextView mInputField;

    private boolean mIs24HourMode;
    private String mHint;
    private int mTextSize;
    private int mHintResId;
    private int mHeaderTextColor;

    /**
     * The number pad will be configured according to the user preference for 24-hour format.
     */
    public static NumberPadTimePickerDialog newInstance(OnTimeSetListener callback) {
        NumberPadTimePickerDialog ret = new NumberPadTimePickerDialog();
        ret.initialize(callback, false /* set24HourModeAtRuntime */, false /* irrelevant */);
        return ret;
    }

    /**
     * The number pad will be configured according to the 24-hour mode specified here.
     */
    public static NumberPadTimePickerDialog newInstance(OnTimeSetListener callback, boolean is24HourMode) {
        NumberPadTimePickerDialog ret = new NumberPadTimePickerDialog();
        ret.initialize(callback, true /* set24HourModeAtRuntime */, is24HourMode);
        return ret;
    }

    private void initialize(OnTimeSetListener callback, boolean set24HourModeAtRuntime, boolean is24HourMode) {
        mTimeSetListener = callback;
        mThemeDark = false;
        mThemeSetAtRuntime = false;
        if (set24HourModeAtRuntime) {
            mIs24HourMode = is24HourMode;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = new BottomSheetNumberPadTimePickerDialog(getActivity(), this, mIs24HourMode);
        return mDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize legacy colors.
        super.onCreateView(inflater, container, savedInstanceState);
        mInputField = (TextView) mDialog.findViewById(R.id.bsp_input_time);
        Preconditions.checkNotNull(mInputField);

        final BottomSheetNumberPadTimePickerDialogThemer themer = mDialog.getThemer();
        // We must create separate Drawables, even for the same color, or else only one
        // of the views will have the drawable applied.
        themer.setHeaderBackground(new ColorDrawable(mHeaderColor))
                .setDivider(new ColorDrawable(mHeaderColor));
        themer.setNumberPadBackground(new ColorDrawable(mBackgroundColor))
                .setBackspaceLocation(BottomSheetNumberPadTimePickerDialogThemer.LOCATION_FOOTER);

        if (mHint != null || mHintResId != 0) {
            if (mHint != null) {
                mInputField.setHint(mHint);
            } else {
                mInputField.setHint(mHintResId);
            }
        }

        if (mTextSize != 0) {
            mInputField.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        }

        final int headerTextColor = mHeaderTextColor != 0
                ? mHeaderTextColor : getDefaultHeaderTextColor();
        themer.setInputTimeTextColor(headerTextColor)
                .setInputAmPmTextColor(headerTextColor);

        // TODO: We still need to apply the accent color as the color control highlight
        // for the number keys, alt keys, and backspace key.
//        mNumpad.setAccentColor(mAccentColor);
        final int disabledColor = ContextCompat.getColor(getActivity(), mThemeDark ?
                R.color.bsp_fab_disabled_dark : R.color.bsp_fab_disabled_light);
        final int[][] states = {{-android.R.attr.state_enabled}, {}};
        final int[] colors = {disabledColor, mAccentColor};
        themer.setFabBackgroundColor(new ColorStateList(states, colors));

//        mNumpad.setTheme(getContext(), mThemeDark);
        final ColorStateList textColors = ContextCompat.getColorStateList(getActivity(), mThemeDark
                ? R.color.bsp_numeric_keypad_button_text_dark
                : R.color.bsp_numeric_keypad_button_text);
        themer.setNumberKeysTextColor(textColors)
                .setAltKeysTextColor(textColors);

        final ColorStateList colorBackspace = ContextCompat.getColorStateList(getActivity(),
                mThemeDark ? R.color.bsp_icon_color_dark : R.color.bsp_icon_color);
        themer.setBackspaceTint(colorBackspace);

        return null;
    }

    @Override
    protected int contentLayout() {
        return 0;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (mTimeSetListener != null) {
            mTimeSetListener.onTimeSet(view, hourOfDay, minute);
        }
    }

    /**
     * Sets the hint of the input time TextView.
     */
    public void setHint(String hint) {
        if (mInputField != null) {
            mInputField.setHint(mHint);
        }
        mHint = hint;
        mHintResId = 0;
    }

    /**
     * Sets the hint of the input time TextView.
     */
    public void setHint(@StringRes int resid) {
        if (mInputField != null) {
            mInputField.setHint(resid);
        }
        mHintResId = resid;
        mHint = null;
    }

    /**
     * Sets the text size in px of the input time TextView.
     */
    public void setInputTextSize(int textSize) {
        if (mInputField != null) {
            mInputField.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        mTextSize = textSize;
    }

    /**
     * @return The TextView that stores the inputted time.
     */
    public TextView getInputTextView() {
        return mInputField;
    }

    /**
     * Set the color of the header text that stores the inputted time.
     */
    public final void setHeaderTextColor(@ColorInt int color) {
        mHeaderTextColor = color;
    }

    private @ColorInt int getDefaultHeaderTextColor() {
        return getDefaultHeaderTextColorSelected();
    }
    
    public static final class Builder extends BottomSheetTimePickerDialog.Builder {
        private final boolean mSet24HourMode;

        private int mHeaderTextColor;

        /**
         * Creates a new {@code Builder} for a {@code NumberPadTimePickerDialog} that will
         * automatically determine whether to use 24-hour mode based on system preferences.
         */
        public Builder(OnTimeSetListener listener) {
            super(listener);
            mSet24HourMode = false;
        }

        /**
         * Creates a new {@code Builder} for a {@code NumberPadTimePickerDialog} that will
         * use or not use 24-hour mode as specified here.
         */
        public Builder(OnTimeSetListener listener, boolean is24HourMode) {
            super(listener, is24HourMode);
            mSet24HourMode = true;
        }

        /**
         * Set the color of the header text that stores the inputted time.
         */
        public Builder setHeaderTextColor(@ColorInt int color) {
            mHeaderTextColor = color;
            return this;
        }

        @Override
        public Builder setAccentColor(int accentColor) {
            return (Builder) super.setAccentColor(accentColor);
        }

        @Override
        public Builder setBackgroundColor(int backgroundColor) {
            return (Builder) super.setBackgroundColor(backgroundColor);
        }

        @Override
        public Builder setHeaderColor(int headerColor) {
            return (Builder) super.setHeaderColor(headerColor);
        }

        @Override
        public Builder setHeaderTextDark(boolean headerTextDark) {
            return (Builder) super.setHeaderTextDark(headerTextDark);
        }

        @Override
        public Builder setThemeDark(boolean themeDark) {
            return (Builder) super.setThemeDark(themeDark);
        }

        @Override
        public NumberPadTimePickerDialog build() {
            NumberPadTimePickerDialog dialog = mSet24HourMode
                    ? newInstance(mListener, mIs24HourMode) : newInstance(mListener);
            super_build(dialog);
            dialog.setHeaderTextColor(mHeaderTextColor);
            return dialog;
        }
    }
}
