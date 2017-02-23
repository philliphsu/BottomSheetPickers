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

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.TimeTextUtils;

/**
 * Dialog to type in a time.
 */
public class NumberPadTimePickerDialog extends BottomSheetTimePickerDialog
        implements NumberPadTimePicker.OnInputChangeListener {
    private static final String TAG = "NumberPadTimePickerDialog";

    private static final String KEY_IS_24_HOUR_VIEW = "is_24_hour_view";
    private static final String KEY_DIGITS_INPUTTED = "digits_inputted";
    private static final String KEY_AMPM_STATE = "ampm_state";
    private static final String KEY_IS_24_HOUR_MODE_SET_AT_RUNTIME = "is_24_hour_mode_set_at_runtime";

    private boolean mIs24HourMode;
    private boolean mIs24HourModeSetAtRuntime;
    /*
     * The digits stored in the numpad from the last time onSaveInstanceState() was called.
     *
     * Why not have the NumberPadTimePicker class save state itself? Because it's a lot more
     * code to do so, as you have to create your own SavedState subclass. Also, we modeled
     * this dialog class on the RadialTimePickerDialog, where the RadialPickerLayout also
     * depends on the dialog to save its state.
     */
    private int[] mInputtedDigits;
    private int mAmPmState = NumberPadTimePicker.UNSPECIFIED;
    private String mHint;
    private int mTextSize;
    private int mHintResId;

    private TextView            mInputField;
    private NumberPadTimePicker mNumpad;

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
        setOnTimeSetListener(callback);
        mThemeDark = false;
        mThemeSetAtRuntime = false;
        mIs24HourModeSetAtRuntime = set24HourModeAtRuntime;
        if (set24HourModeAtRuntime) {
            mIs24HourMode = is24HourMode;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mInputtedDigits = savedInstanceState.getIntArray(KEY_DIGITS_INPUTTED);
            mIs24HourMode = savedInstanceState.getBoolean(KEY_IS_24_HOUR_VIEW);
            mAmPmState = savedInstanceState.getInt(KEY_AMPM_STATE);
            mIs24HourModeSetAtRuntime = savedInstanceState.getBoolean(KEY_IS_24_HOUR_MODE_SET_AT_RUNTIME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mInputField = (TextView) view.findViewById(R.id.input_time);
        mNumpad = (NumberPadTimePicker) view.findViewById(R.id.number_grid);

        final FloatingActionButton fab = (FloatingActionButton) mNumpad.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mNumpad.checkTimeValid())
                    return;
                onTimeSet(mNumpad, mNumpad.getHour(), mNumpad.getMinute());
            }
        });

        if (mIs24HourModeSetAtRuntime) {
            mNumpad.setIs24HourMode(mIs24HourMode);
        }
        mNumpad.setOnInputChangeListener(this);
        mNumpad.insertDigits(mInputtedDigits); // TOneverDO: before mNumpad.setOnInputChangeListener(this);
        mNumpad.setAmPmState(mAmPmState);

        view.findViewById(R.id.input_time_container).setBackgroundColor(mHeaderColor);

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

        mInputField.setTextColor(mHeaderTextDark? mBlackText : mWhite);
        mNumpad.setAccentColor(mAccentColor);
        mNumpad.setTheme(getContext()/*DO NOT GIVE THE APPLICATION CONTEXT, OR ELSE THE NUMPAD
        CAN'T GET THE CORRECT ACCENT COLOR*/, mThemeDark);

        return view;
    }

    @Override
    protected int contentLayout() {
        return R.layout.dialog_time_picker_numpad;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNumpad != null) {
            outState.putIntArray(KEY_DIGITS_INPUTTED, mNumpad.getDigits());
            outState.putBoolean(KEY_IS_24_HOUR_VIEW, mIs24HourMode);
            outState.putInt(KEY_AMPM_STATE, mNumpad.getAmPmState());
            outState.putBoolean(KEY_IS_24_HOUR_MODE_SET_AT_RUNTIME, mIs24HourModeSetAtRuntime);
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

    @Override
    public void onDigitInserted(String newStr) {
        updateInputText(newStr);
    }

    @Override
    public void onDigitDeleted(String newStr) {
        updateInputText(newStr);
    }

    @Override
    public void onDigitsCleared() {
        updateInputText("");
    }

    @Override
    public void onInputDisabled() {
        // No implementation.
    }

    private void updateInputText(String inputText) {
        TimeTextUtils.setText(inputText, mInputField);
    }
}
