/*
 * Copyright (C) 2013 The Android Open Source Project
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
 * limitations under the License
 */

package com.philliphsu.bottomsheetpickers.time.grid;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.Utils;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A derivative of the AOSP datetimepicker TimePickerDialog class.
 */
public class GridTimePickerDialog extends BottomSheetTimePickerDialog
        implements GridPickerLayout.OnValueSelectedListener {
    private static final String TAG = "TimePickerDialog";

    private static final String KEY_HOUR_OF_DAY = "hour_of_day";
    private static final String KEY_MINUTE = "minute";
    private static final String KEY_IS_24_HOUR_VIEW = "is_24_hour_view";
    private static final String KEY_CURRENT_ITEM_SHOWING = "current_item_showing";
    private static final String KEY_IN_KB_MODE = "in_kb_mode";
    private static final String KEY_TYPED_TIMES = "typed_times";
    private static final String KEY_HEADER_TEXT_COLOR_SELECTED = "header_text_color_selected";
    private static final String KEY_HEADER_TEXT_COLOR_UNSELECTED = "header_text_color_unselected";
    private static final String KEY_TIME_SEPARATOR_COLOR = "time_separator_color";
    private static final String KEY_HALF_DAY_BUTTON_COLOR_SELECTED = "half_day_button_color_selected";
    private static final String KEY_HALF_DAY_BUTTON_COLOR_UNSELECTED = "half_day_button_color_unselected";

    public static final int HOUR_INDEX = 0;
    public static final int MINUTE_INDEX = 1;
    // NOT a real index for the purpose of what's showing.
    public static final int HALF_DAY_INDEX = 2;
    // Also NOT a real index, just used for keyboard mode.
    public static final int ENABLE_PICKER_INDEX = 3;
    public static final int HALF_DAY_1 = 0;
    public static final int HALF_DAY_2 = 1;

    // TODO: Restore
//    private HapticFeedbackController mHapticFeedbackController;

    private TextView mHourView;
    private TextView mHourSpaceView;
    private TextView mMinuteView;
    private TextView mMinuteSpaceView;
    private LinearLayout mAmPmToggles;
    private TextView mAmTextView;
    private TextView mPmTextView;
    private View mAmPmHitspace;
    private LinearLayout mHalfDayToggles;
    private ImageView mFirstHalfDayToggle;
    private ImageView mSecondHalfDayToggle;
    private View mHalfDaysHitspace;
    private GridPickerLayout mTimePicker;
    private FloatingActionButton mDoneButton;

    private int mSelectedColor;
    private int mUnselectedColor;
    private int mHalfDaySelectedColor;
    private int mHalfDayUnselectedColor;

    private int mHeaderTextColorSelected;
    private int mHeaderTextColorUnselected;
    private int mTimeSeparatorColor;
    private int mHalfDayButtonColorSelected;
    private int mHalfDayButtonColorUnselected;
    private String mAmText;
    private String mPmText;

    private boolean mAllowAutoAdvance;
    private int mInitialHourOfDay;
    private int mInitialMinute;
    private boolean mIs24HourMode;

    // For hardware IME input.
    private char mPlaceholderText;
    private String mDoublePlaceholderText;
    private String mDeletedKeyFormat;
    private boolean mInKbMode;
    private ArrayList<Integer> mTypedTimes;
    private Node mLegalTimesTree;
    private int mAmKeyCode;
    private int mPmKeyCode;

    // Accessibility strings.
    private String mHourPickerDescription;
    private String mSelectHours;
    private String mMinutePickerDescription;
    private String mSelectMinutes;

    @Override
    protected int contentLayout() {
        return R.layout.bsp_dialog_time_picker_grid;
    }

    /**
     * @param callback      How the parent is notified that the time is set.
     * @param hourOfDay     The initial hour-of-day of the dialog.
     * @param minute        The initial minute of the dialog.
     * @param is24HourMode  Whether the dialog should be configured for 24-hour mode.
     */
    public static GridTimePickerDialog newInstance(OnTimeSetListener callback,
                                                   int hourOfDay, int minute, boolean is24HourMode) {
        GridTimePickerDialog ret = new GridTimePickerDialog();
        ret.initialize(callback, hourOfDay, minute, is24HourMode);
        return ret;
    }

    public void initialize(OnTimeSetListener callback,
            int hourOfDay, int minute, boolean is24HourMode) {
        setOnTimeSetListener(callback);
        mInitialHourOfDay = hourOfDay;
        mInitialMinute = minute;
        mIs24HourMode = is24HourMode;
        mInKbMode = false;
        mThemeDark = false;
        mThemeSetAtRuntime = false;
    }

    public void setStartTime(int hourOfDay, int minute) {
        mInitialHourOfDay = hourOfDay;
        mInitialMinute = minute;
        mInKbMode = false;
    }

    /**
     * Set the color of the header text when it is selected.
     */
    public final void setHeaderTextColorSelected(@ColorInt int color) {
        mHeaderTextColorSelected = color;
    }

    /**
     * Set the color of the header text when it is not selected.
     */
    public final void setHeaderTextColorUnselected(@ColorInt int color) {
        mHeaderTextColorUnselected = color;
    }

    /**
     * Set the color of the time separator that separates the hour and minute views in the header.
     */
    public final void setTimeSeparatorColor(@ColorInt int color) {
        mTimeSeparatorColor = color;
    }

    /**
     * Set the color of the AM/PM text when it is selected. This is equivalent to
     * {@link #setHalfDayButtonColorSelected(int)} in 24-hour time.
     */
    public final void setAmPmTextColorSelected(@ColorInt int color) {
        setHalfDayButtonColorSelected(color);
    }

    /**
     * Set the color of the AM/PM text when it is not selected. This is equivalent to
     * {@link #setHalfDayButtonColorUnselected(int)} in 24-hour time.
     */
    public final void setAmPmTextColorUnselected(@ColorInt int color) {
        setHalfDayButtonColorUnselected(color);
    }

    /**
     * Set the color of the half-day image button when it is selected. This is equivalent to
     * {@link #setAmPmTextColorSelected(int)} in 12-hour time.
     */
    public final void setHalfDayButtonColorSelected(@ColorInt int color) {
        mHalfDayButtonColorSelected = color;
    }

    /**
     * Set the color of the half-day image button when it is not selected. This is equivalent to
     * {@link #setAmPmTextColorUnselected(int)} in 12-hour time.
     */
    public final void setHalfDayButtonColorUnselected(@ColorInt int color) {
        mHalfDayButtonColorUnselected = color;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_HOUR_OF_DAY)
                    && savedInstanceState.containsKey(KEY_MINUTE)
                    && savedInstanceState.containsKey(KEY_IS_24_HOUR_VIEW)) {
            mInitialHourOfDay = savedInstanceState.getInt(KEY_HOUR_OF_DAY);
            mInitialMinute = savedInstanceState.getInt(KEY_MINUTE);
            mIs24HourMode = savedInstanceState.getBoolean(KEY_IS_24_HOUR_VIEW);
            mInKbMode = savedInstanceState.getBoolean(KEY_IN_KB_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        KeyboardListener keyboardListener = new KeyboardListener();
        view.findViewById(R.id.bsp_time_picker_dialog).setOnKeyListener(keyboardListener);

        if (!mThemeSetAtRuntime) {
            mThemeDark = Utils.isDarkTheme(getActivity(), mThemeDark);
        }

        final Resources res = getResources();
        final Context ctx = getActivity();
        mHourPickerDescription = res.getString(R.string.bsp_hour_picker_description);
        mSelectHours = res.getString(R.string.bsp_select_hours);
        mMinutePickerDescription = res.getString(R.string.bsp_minute_picker_description);
        mSelectMinutes = res.getString(R.string.bsp_select_minutes);

        mHourView = (TextView) view.findViewById(R.id.bsp_hours);
        mHourView.setOnKeyListener(keyboardListener);
        mHourSpaceView = (TextView) view.findViewById(R.id.bsp_hour_space);
        mMinuteSpaceView = (TextView) view.findViewById(R.id.bsp_minutes_space);
        mMinuteView = (TextView) view.findViewById(R.id.bsp_minutes);
        mMinuteView.setOnKeyListener(keyboardListener);
        // TODO: setOnKeyListener?
        mAmPmToggles = (LinearLayout) view.findViewById(R.id.bsp_ampm_toggles);
        mAmTextView = (TextView) view.findViewById(R.id.bsp_am_label);
        mAmTextView.setOnKeyListener(keyboardListener);
        mPmTextView = (TextView) view.findViewById(R.id.bsp_pm_label);
        mPmTextView.setOnKeyListener(keyboardListener);
        String[] amPmTexts = new DateFormatSymbols().getAmPmStrings();
        mAmText = amPmTexts[0];
        mPmText = amPmTexts[1];
        // TODO: Check AOSP code to see how to get abbreviated AM/PM translations.
        mAmTextView.setText(mAmText);
        mPmTextView.setText(mPmText);
        // TODO: setOnKeyListener?
        mHalfDayToggles = (LinearLayout) view.findViewById(R.id.bsp_half_day_toggles);
        mFirstHalfDayToggle = (ImageView) view.findViewById(R.id.bsp_half_day_toggle_1);
        mFirstHalfDayToggle.setOnKeyListener(keyboardListener);
        mSecondHalfDayToggle = (ImageView) view.findViewById(R.id.bsp_half_day_toggle_2);
        mSecondHalfDayToggle.setOnKeyListener(keyboardListener);

        // TODO: Restore
//        mHapticFeedbackController = new HapticFeedbackController(getActivity());
        mTimePicker = (GridPickerLayout) view.findViewById(R.id.bsp_time_picker);
        mTimePicker.setOnValueSelectedListener(this);
        mTimePicker.setOnKeyListener(keyboardListener);
        mTimePicker.initialize(getActivity(), /*mHapticFeedbackController,*/ mInitialHourOfDay,
            mInitialMinute, mIs24HourMode);

        int currentItemShowing = HOUR_INDEX;
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_CURRENT_ITEM_SHOWING)) {
                currentItemShowing = savedInstanceState.getInt(KEY_CURRENT_ITEM_SHOWING);
            }
            mHeaderTextColorSelected = savedInstanceState.getInt(KEY_HEADER_TEXT_COLOR_SELECTED);
            mHeaderTextColorUnselected = savedInstanceState.getInt(KEY_HEADER_TEXT_COLOR_UNSELECTED);
            mHalfDayButtonColorSelected = savedInstanceState.getInt(KEY_HALF_DAY_BUTTON_COLOR_SELECTED);
            mHalfDayButtonColorUnselected = savedInstanceState.getInt(KEY_HALF_DAY_BUTTON_COLOR_UNSELECTED);
            mTimeSeparatorColor = savedInstanceState.getInt(KEY_TIME_SEPARATOR_COLOR);
        }

        mHourView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentItemShowing(HOUR_INDEX, true, false, true);
                tryVibrate();
            }
        });
        mMinuteView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentItemShowing(MINUTE_INDEX, true, false, true);
                tryVibrate();
            }
        });

        mDoneButton = (FloatingActionButton) view.findViewById(R.id.bsp_fab);
        mDoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInKbMode && isTypedTimeFullyLegal()) {
                    finishKbMode(false);
                } else {
                    tryVibrate();
                }
                onTimeSet(mTimePicker, mTimePicker.getHours(), mTimePicker.getMinutes());
            }
        });
        mDoneButton.setOnKeyListener(keyboardListener);

        mAmPmHitspace = view.findViewById(R.id.bsp_ampm_hitspace);
        mHalfDaysHitspace = view.findViewById(R.id.bsp_half_days_hitspace);
        mAmPmHitspace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tryVibrate();
                int amOrPm = mTimePicker.getIsCurrentlyAmOrPm();
                if (amOrPm == HALF_DAY_1) {
                    amOrPm = HALF_DAY_2;
                } else if (amOrPm == HALF_DAY_2) {
                    amOrPm = HALF_DAY_1;
                }
                updateAmPmDisplay(amOrPm);
                mTimePicker.setHalfDay(amOrPm);
            }
        });
        mHalfDaysHitspace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tryVibrate();
                int amOrPm = mTimePicker.getIsCurrentlyAmOrPm();
                if (amOrPm == HALF_DAY_1) {
                    amOrPm = HALF_DAY_2;
                } else if (amOrPm == HALF_DAY_2) {
                    amOrPm = HALF_DAY_1;
                }
                updateAmPmDisplay(amOrPm);
                mTimePicker.setHalfDay(amOrPm);
            }
        });

        mHalfDaysHitspace.setVisibility(mIs24HourMode ? View.VISIBLE : View.GONE);
        mHalfDayToggles.setVisibility(mIs24HourMode ? View.VISIBLE : View.GONE);
        mAmPmHitspace.setVisibility(mIs24HourMode ? View.GONE : View.VISIBLE);
        mAmPmToggles.setVisibility(mIs24HourMode ? View.GONE : View.VISIBLE);

        mAllowAutoAdvance = true;
        setHour(mInitialHourOfDay, true);
        setMinute(mInitialMinute);

        // Set up for keyboard mode.
        mDoublePlaceholderText = res.getString(R.string.bsp_time_placeholder);
        mDeletedKeyFormat = res.getString(R.string.bsp_deleted_key);
        mPlaceholderText = mDoublePlaceholderText.charAt(0);
        mAmKeyCode = mPmKeyCode = -1;
        generateLegalTimesTree();
        if (mInKbMode) {
            mTypedTimes = savedInstanceState.getIntegerArrayList(KEY_TYPED_TIMES);
            tryStartingKbMode(-1);
            mHourView.invalidate();
        } else if (mTypedTimes == null) {
            mTypedTimes = new ArrayList<Integer>();
        }

        // Prepare default header text colors.
        final int defaultSelectedColor = getDefaultHeaderTextColorSelected();
        final int defaultUnselectedColor = getDefaultHeaderTextColorUnselected();

        mSelectedColor = mHeaderTextColorSelected != 0
                ? mHeaderTextColorSelected : defaultSelectedColor;
        mUnselectedColor = mHeaderTextColorUnselected != 0
                ? mHeaderTextColorUnselected : defaultUnselectedColor;

        mHalfDaySelectedColor = mHalfDayButtonColorSelected != 0
                ? mHalfDayButtonColorSelected : defaultSelectedColor;
        mHalfDayUnselectedColor = mHalfDayButtonColorUnselected != 0
                ? mHalfDayButtonColorUnselected : defaultUnselectedColor;

        mTimePicker.setAccentColor(mAccentColor);
        mTimePicker.setTheme(getActivity().getApplicationContext(), mThemeDark);

        // Set the colors for each view based on the theme.
        view.findViewById(R.id.bsp_time_display_background).setBackgroundColor(mHeaderColor);
        view.findViewById(R.id.bsp_time_display).setBackgroundColor(mHeaderColor);
        ((TextView) view.findViewById(R.id.bsp_separator)).setTextColor(mTimeSeparatorColor != 0
                ? mTimeSeparatorColor : (mHeaderTextDark ? mBlackTextDisabled : mWhiteTextDisabled));

        // Color in normal state
        mDoneButton.setBackgroundTintList(ColorStateList.valueOf(mAccentColor));

        // Set current item at the end when the header text colors have been initialized.
        setCurrentItemShowing(currentItemShowing, false, true, true);
        // Update the half day at the end when the state colors have been initialized.
        updateAmPmDisplay(mInitialHourOfDay < 12? HALF_DAY_1 : HALF_DAY_2);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        mHapticFeedbackController.start();
    }

    @Override
    public void onPause() {
        super.onPause();
//        mHapticFeedbackController.stop();
    }

    public void tryVibrate() {
//        mHapticFeedbackController.tryVibrate();
    }

    private void updateAmPmDisplay(int amOrPm) {
        int firstColor = amOrPm == HALF_DAY_1 ? mHalfDaySelectedColor : mHalfDayUnselectedColor;
        int secondColor = amOrPm == HALF_DAY_2 ? mHalfDaySelectedColor : mHalfDayUnselectedColor;

        if (mIs24HourMode) {
            final Drawable firstHalfDayToggle = mFirstHalfDayToggle.getDrawable();
            final Drawable secondHalfDayToggle = mSecondHalfDayToggle.getDrawable();
            if (Utils.checkApiLevel(Build.VERSION_CODES.LOLLIPOP)) {
                firstHalfDayToggle.setTint(firstColor);
                secondHalfDayToggle.setTint(secondColor);
            } else {
                // Ignore the Lint warning that says the casting is redundant;
                // it is in fact necessary.
                ((VectorDrawableCompat) firstHalfDayToggle).setTint(firstColor);
                ((VectorDrawableCompat) secondHalfDayToggle).setTint(secondColor);
            }
        } else {
            mAmTextView.setTextColor(firstColor);
            mPmTextView.setTextColor(secondColor);
        }

        if (amOrPm == HALF_DAY_1) {
            Utils.tryAccessibilityAnnounce(mTimePicker, mAmText);
            mAmPmHitspace.setContentDescription(mAmText);
        } else if (amOrPm == HALF_DAY_2) {
            Utils.tryAccessibilityAnnounce(mTimePicker, mPmText);
            mAmPmHitspace.setContentDescription(mPmText);
        } else {
            mAmTextView.setText(mDoublePlaceholderText);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTimePicker != null) {
            outState.putInt(KEY_HOUR_OF_DAY, mTimePicker.getHours());
            outState.putInt(KEY_MINUTE, mTimePicker.getMinutes());
            outState.putBoolean(KEY_IS_24_HOUR_VIEW, mIs24HourMode);
            outState.putInt(KEY_CURRENT_ITEM_SHOWING, mTimePicker.getCurrentItemShowing());
            outState.putBoolean(KEY_IN_KB_MODE, mInKbMode);
            if (mInKbMode) {
                outState.putIntegerArrayList(KEY_TYPED_TIMES, mTypedTimes);
            }
            outState.putInt(KEY_HEADER_TEXT_COLOR_SELECTED, mHeaderTextColorSelected);
            outState.putInt(KEY_HEADER_TEXT_COLOR_UNSELECTED, mHeaderTextColorUnselected);
            outState.putInt(KEY_TIME_SEPARATOR_COLOR, mTimeSeparatorColor);
            outState.putInt(KEY_HALF_DAY_BUTTON_COLOR_SELECTED, mHalfDayButtonColorSelected);
            outState.putInt(KEY_HALF_DAY_BUTTON_COLOR_UNSELECTED, mHalfDayButtonColorUnselected);
        }
    }

    /**
     * Called by the picker for updating the header display.
     */
    @Override
    public void onValueSelected(int pickerIndex, int newValue, boolean autoAdvance) {
        if (pickerIndex == HOUR_INDEX) {
            setHour(newValue, false);
            String announcement = String.format("%d", newValue);
            if (mAllowAutoAdvance && autoAdvance) {
                setCurrentItemShowing(MINUTE_INDEX, true, true, false);
                announcement += ". " + mSelectMinutes;
            } else {
                mTimePicker.setContentDescription(mHourPickerDescription + ": " + newValue);
            }

            Utils.tryAccessibilityAnnounce(mTimePicker, announcement);
        } else if (pickerIndex == MINUTE_INDEX){
            setMinute(newValue);
            mTimePicker.setContentDescription(mMinutePickerDescription + ": " + newValue);
        } else if (pickerIndex == HALF_DAY_INDEX) {
            updateAmPmDisplay(newValue);
        } else if (pickerIndex == ENABLE_PICKER_INDEX) {
            if (!isTypedTimeFullyLegal()) {
                mTypedTimes.clear();
            }
            finishKbMode(true);
        }
    }

    private void setHour(int value, boolean announce) {
        String format;
        if (mIs24HourMode) {
            format = "%02d";
        } else {
            format = "%d";
            value = value % 12;
            if (value == 0) {
                value = 12;
            }
        }

        CharSequence text = String.format(format, value);
        mHourView.setText(text);
        mHourSpaceView.setText(text);
        if (announce) {
            Utils.tryAccessibilityAnnounce(mTimePicker, text);
        }
    }

    private void setMinute(int value) {
        if (value == 60) {
            value = 0;
        }
        CharSequence text = String.format(Locale.getDefault(), "%02d", value);
        Utils.tryAccessibilityAnnounce(mTimePicker, text);
        mMinuteView.setText(text);
        mMinuteSpaceView.setText(text);
    }

    // Show either Hours or Minutes.
    private void setCurrentItemShowing(int index, boolean animateCircle, boolean delayLabelAnimate,
            boolean announce) {
        mTimePicker.setCurrentItemShowing(index, animateCircle);

        if (index == HOUR_INDEX) {
            int hours = mTimePicker.getHours();
            if (!mIs24HourMode) {
                hours = hours % 12;
            }
            mTimePicker.setContentDescription(mHourPickerDescription + ": " + hours);
            if (announce) {
                Utils.tryAccessibilityAnnounce(mTimePicker, mSelectHours);
            }
        } else {
            int minutes = mTimePicker.getMinutes();
            mTimePicker.setContentDescription(mMinutePickerDescription + ": " + minutes);
            if (announce) {
                Utils.tryAccessibilityAnnounce(mTimePicker, mSelectMinutes);
            }
        }

        int hourColor = (index == HOUR_INDEX)? mSelectedColor : mUnselectedColor;
        int minuteColor = (index == MINUTE_INDEX)? mSelectedColor : mUnselectedColor;
        mHourView.setTextColor(hourColor);
        mMinuteView.setTextColor(minuteColor);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // END RELEVANT CODE
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * For keyboard mode, processes key events.
     * @param keyCode the pressed key.
     * @return true if the key was successfully processed, false otherwise.
     */
    private boolean processKeyUp(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_ESCAPE || keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_TAB) {
            if(mInKbMode) {
                if (isTypedTimeFullyLegal()) {
                    finishKbMode(true);
                }
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (mInKbMode) {
                if (!isTypedTimeFullyLegal()) {
                    return true;
                }
                finishKbMode(false);
            }
            onTimeSet(mTimePicker, mTimePicker.getHours(), mTimePicker.getMinutes());
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (mInKbMode) {
                if (!mTypedTimes.isEmpty()) {
                    int deleted = deleteLastTypedKey();
                    String deletedKeyStr;
                    if (deleted == getAmOrPmKeyCode(HALF_DAY_1)) {
                        deletedKeyStr = mAmText;
                    } else if (deleted == getAmOrPmKeyCode(HALF_DAY_2)) {
                        deletedKeyStr = mPmText;
                    } else {
                        deletedKeyStr = String.format("%d", getValFromKeyCode(deleted));
                    }
                    Utils.tryAccessibilityAnnounce(mTimePicker,
                            String.format(mDeletedKeyFormat, deletedKeyStr));
                    updateDisplay(true);
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_0 || keyCode == KeyEvent.KEYCODE_1
                || keyCode == KeyEvent.KEYCODE_2 || keyCode == KeyEvent.KEYCODE_3
                || keyCode == KeyEvent.KEYCODE_4 || keyCode == KeyEvent.KEYCODE_5
                || keyCode == KeyEvent.KEYCODE_6 || keyCode == KeyEvent.KEYCODE_7
                || keyCode == KeyEvent.KEYCODE_8 || keyCode == KeyEvent.KEYCODE_9
                || (!mIs24HourMode &&
                        (keyCode == getAmOrPmKeyCode(HALF_DAY_1) || keyCode == getAmOrPmKeyCode(HALF_DAY_2)))) {
            if (!mInKbMode) {
                if (mTimePicker == null) {
                    // Something's wrong, because time picker should definitely not be null.
                    Log.e(TAG, "Unable to initiate keyboard mode, TimePicker was null.");
                    return true;
                }
                mTypedTimes.clear();
                tryStartingKbMode(keyCode);
                return true;
            }
            // We're already in keyboard mode.
            if (addKeyIfLegal(keyCode)) {
                updateDisplay(false);
            }
            return true;
        }
        return false;
    }

    /**
     * Try to start keyboard mode with the specified key, as long as the timepicker is not in the
     * middle of a touch-event.
     * @param keyCode The key to use as the first press. Keyboard mode will not be started if the
     * key is not legal to start with. Or, pass in -1 to get into keyboard mode without a starting
     * key.
     */
    private void tryStartingKbMode(int keyCode) {
        // TODO: What is this method?
        if (/*mTimePicker.trySettingInputEnabled(false) &&*/
                (keyCode == -1 || addKeyIfLegal(keyCode))) {
            mInKbMode = true;
            mDoneButton.setEnabled(false);
            updateDisplay(false);
        }
    }

    private boolean addKeyIfLegal(int keyCode) {
        // If we're in 24hour mode, we'll need to check if the input is full. If in AM/PM mode,
        // we'll need to see if AM/PM have been typed.
        if ((mIs24HourMode && mTypedTimes.size() == 4) ||
                (!mIs24HourMode && isTypedTimeFullyLegal())) {
            return false;
        }

        mTypedTimes.add(keyCode);
        if (!isTypedTimeLegalSoFar()) {
            deleteLastTypedKey();
            return false;
        }

        int val = getValFromKeyCode(keyCode);
        Utils.tryAccessibilityAnnounce(mTimePicker, String.format("%d", val));
        // Automatically fill in 0's if AM or PM was legally entered.
        if (isTypedTimeFullyLegal()) {
            if (!mIs24HourMode && mTypedTimes.size() <= 3) {
                mTypedTimes.add(mTypedTimes.size() - 1, KeyEvent.KEYCODE_0);
                mTypedTimes.add(mTypedTimes.size() - 1, KeyEvent.KEYCODE_0);
            }
            mDoneButton.setEnabled(true);
        }

        return true;
    }

    /**
     * Traverse the tree to see if the keys that have been typed so far are legal as is,
     * or may become legal as more keys are typed (excluding backspace).
     */
    private boolean isTypedTimeLegalSoFar() {
        Node node = mLegalTimesTree;
        for (int keyCode : mTypedTimes) {
            node = node.canReach(keyCode);
            if (node == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the time that has been typed so far is completely legal, as is.
     */
    private boolean isTypedTimeFullyLegal() {
        if (mIs24HourMode) {
            // For 24-hour mode, the time is legal if the hours and minutes are each legal. Note:
            // getEnteredTime() will ONLY call isTypedTimeFullyLegal() when NOT in 24hour mode.
            int[] values = getEnteredTime(null);
            return (values[0] >= 0 && values[1] >= 0 && values[1] < 60);
        } else {
            // For AM/PM mode, the time is legal if it contains an AM or PM, as those can only be
            // legally added at specific times based on the tree's algorithm.
            return (mTypedTimes.contains(getAmOrPmKeyCode(HALF_DAY_1)) ||
                    mTypedTimes.contains(getAmOrPmKeyCode(HALF_DAY_2)));
        }
    }

    private int deleteLastTypedKey() {
        int deleted = mTypedTimes.remove(mTypedTimes.size() - 1);
        if (!isTypedTimeFullyLegal()) {
            mDoneButton.setEnabled(false);
        }
        return deleted;
    }

    /**
     * Get out of keyboard mode. If there is nothing in typedTimes, revert to TimePicker's time.
     * @param updateDisplays If true, update the displays with the relevant time.
     */
    private void finishKbMode(boolean updateDisplays) {
        mInKbMode = false;
        if (!mTypedTimes.isEmpty()) {
            int values[] = getEnteredTime(null);
            mTimePicker.setTime(values[0], values[1]);
            if (!mIs24HourMode) {
                mTimePicker.setHalfDay(values[2]);
            }
            mTypedTimes.clear();
        }
        if (updateDisplays) {
            updateDisplay(false);
            // TODO: What is this method?
//            mTimePicker.trySettingInputEnabled(true);
        }
    }

    /**
     * Update the hours, minutes, and AM/PM displays with the typed times. If the typedTimes is
     * empty, either show an empty display (filled with the placeholder text), or update from the
     * timepicker's values.
     * @param allowEmptyDisplay if true, then if the typedTimes is empty, use the placeholder text.
     * Otherwise, revert to the timepicker's values.
     */
    private void updateDisplay(boolean allowEmptyDisplay) {
        if (!allowEmptyDisplay && mTypedTimes.isEmpty()) {
            int hour = mTimePicker.getHours();
            int minute = mTimePicker.getMinutes();
            setHour(hour, true);
            setMinute(minute);
            if (!mIs24HourMode) {
                updateAmPmDisplay(hour < 12? HALF_DAY_1 : HALF_DAY_2);
            }
            setCurrentItemShowing(mTimePicker.getCurrentItemShowing(), true, true, true);
            mDoneButton.setEnabled(true);
        } else {
            Boolean[] enteredZeros = {false, false};
            int[] values = getEnteredTime(enteredZeros);
            String hourFormat = enteredZeros[0]? "%02d" : "%2d";
            String minuteFormat = (enteredZeros[1])? "%02d" : "%2d";
            String hourStr = (values[0] == -1)? mDoublePlaceholderText :
                String.format(hourFormat, values[0]).replace(' ', mPlaceholderText);
            String minuteStr = (values[1] == -1)? mDoublePlaceholderText :
                String.format(minuteFormat, values[1]).replace(' ', mPlaceholderText);
            mHourView.setText(hourStr);
            mHourSpaceView.setText(hourStr);
            mHourView.setTextColor(mUnselectedColor);
            mMinuteView.setText(minuteStr);
            mMinuteSpaceView.setText(minuteStr);
            mMinuteView.setTextColor(mUnselectedColor);
            if (!mIs24HourMode) {
                updateAmPmDisplay(values[2]);
            }
        }
    }

    private static int getValFromKeyCode(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                return 0;
            case KeyEvent.KEYCODE_1:
                return 1;
            case KeyEvent.KEYCODE_2:
                return 2;
            case KeyEvent.KEYCODE_3:
                return 3;
            case KeyEvent.KEYCODE_4:
                return 4;
            case KeyEvent.KEYCODE_5:
                return 5;
            case KeyEvent.KEYCODE_6:
                return 6;
            case KeyEvent.KEYCODE_7:
                return 7;
            case KeyEvent.KEYCODE_8:
                return 8;
            case KeyEvent.KEYCODE_9:
                return 9;
            default:
                return -1;
        }
    }

    /**
     * Get the currently-entered time, as integer values of the hours and minutes typed.
     * @param enteredZeros A size-2 boolean array, which the caller should initialize, and which
     * may then be used for the caller to know whether zeros had been explicitly entered as either
     * hours of minutes. This is helpful for deciding whether to show the dashes, or actual 0's.
     * @return A size-3 int array. The first value will be the hours, the second value will be the
     * minutes, and the third will be either TimePickerDialog.AM or TimePickerDialog.PM.
     */
    private int[] getEnteredTime(Boolean[] enteredZeros) {
        int amOrPm = -1;
        int startIndex = 1;
        if (!mIs24HourMode && isTypedTimeFullyLegal()) {
            int keyCode = mTypedTimes.get(mTypedTimes.size() - 1);
            if (keyCode == getAmOrPmKeyCode(HALF_DAY_1)) {
                amOrPm = HALF_DAY_1;
            } else if (keyCode == getAmOrPmKeyCode(HALF_DAY_2)){
                amOrPm = HALF_DAY_2;
            }
            startIndex = 2;
        }
        int minute = -1;
        int hour = -1;
        for (int i = startIndex; i <= mTypedTimes.size(); i++) {
            int val = getValFromKeyCode(mTypedTimes.get(mTypedTimes.size() - i));
            if (i == startIndex) {
                minute = val;
            } else if (i == startIndex+1) {
                minute += 10*val;
                if (enteredZeros != null && val == 0) {
                    enteredZeros[1] = true;
                }
            } else if (i == startIndex+2) {
                hour = val;
            } else if (i == startIndex+3) {
                hour += 10*val;
                if (enteredZeros != null && val == 0) {
                    enteredZeros[0] = true;
                }
            }
        }

        int[] ret = {hour, minute, amOrPm};
        return ret;
    }

    /**
     * Get the keycode value for AM and PM in the current language.
     */
    private int getAmOrPmKeyCode(int amOrPm) {
        // Cache the codes.
        if (mAmKeyCode == -1 || mPmKeyCode == -1) {
            // Find the first character in the AM/PM text that is unique.
            KeyCharacterMap kcm = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
            char amChar;
            char pmChar;
            for (int i = 0; i < Math.max(mAmText.length(), mPmText.length()); i++) {
                amChar = mAmText.toLowerCase(Locale.getDefault()).charAt(i);
                pmChar = mPmText.toLowerCase(Locale.getDefault()).charAt(i);
                if (amChar != pmChar) {
                    KeyEvent[] events = kcm.getEvents(new char[]{amChar, pmChar});
                    // There should be 4 events: a down and up for both AM and PM.
                    if (events != null && events.length == 4) {
                        mAmKeyCode = events[0].getKeyCode();
                        mPmKeyCode = events[2].getKeyCode();
                    } else {
                        Log.e(TAG, "Unable to find keycodes for AM and PM.");
                    }
                    break;
                }
            }
        }
        if (amOrPm == HALF_DAY_1) {
            return mAmKeyCode;
        } else if (amOrPm == HALF_DAY_2) {
            return mPmKeyCode;
        }

        return -1;
    }

    /**
     * Create a tree for deciding what keys can legally be typed.
     */
    private void generateLegalTimesTree() {
        // Create a quick cache of numbers to their keycodes.
        int k0 = KeyEvent.KEYCODE_0;
        int k1 = KeyEvent.KEYCODE_1;
        int k2 = KeyEvent.KEYCODE_2;
        int k3 = KeyEvent.KEYCODE_3;
        int k4 = KeyEvent.KEYCODE_4;
        int k5 = KeyEvent.KEYCODE_5;
        int k6 = KeyEvent.KEYCODE_6;
        int k7 = KeyEvent.KEYCODE_7;
        int k8 = KeyEvent.KEYCODE_8;
        int k9 = KeyEvent.KEYCODE_9;

        // The root of the tree doesn't contain any numbers.
        mLegalTimesTree = new Node();
        if (mIs24HourMode) {
            // We'll be re-using these nodes, so we'll save them.
            Node minuteFirstDigit = new Node(k0, k1, k2, k3, k4, k5);
            Node minuteSecondDigit = new Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9);
            // The first digit must be followed by the second digit.
            minuteFirstDigit.addChild(minuteSecondDigit);

            // The first digit may be 0-1.
            Node firstDigit = new Node(k0, k1);
            mLegalTimesTree.addChild(firstDigit);

            // When the first digit is 0-1, the second digit may be 0-5.
            Node secondDigit = new Node(k0, k1, k2, k3, k4, k5);
            firstDigit.addChild(secondDigit);
            // We may now be followed by the first minute digit. E.g. 00:09, 15:58.
            secondDigit.addChild(minuteFirstDigit);

            // When the first digit is 0-1, and the second digit is 0-5, the third digit may be 6-9.
            Node thirdDigit = new Node(k6, k7, k8, k9);
            // The time must now be finished. E.g. 0:55, 1:08.
            secondDigit.addChild(thirdDigit);

            // When the first digit is 0-1, the second digit may be 6-9.
            secondDigit = new Node(k6, k7, k8, k9);
            firstDigit.addChild(secondDigit);
            // We must now be followed by the first minute digit. E.g. 06:50, 18:20.
            secondDigit.addChild(minuteFirstDigit);

            // The first digit may be 2.
            firstDigit = new Node(k2);
            mLegalTimesTree.addChild(firstDigit);

            // When the first digit is 2, the second digit may be 0-3.
            secondDigit = new Node(k0, k1, k2, k3);
            firstDigit.addChild(secondDigit);
            // We must now be followed by the first minute digit. E.g. 20:50, 23:09.
            secondDigit.addChild(minuteFirstDigit);

            // When the first digit is 2, the second digit may be 4-5.
            secondDigit = new Node(k4, k5);
            firstDigit.addChild(secondDigit);
            // We must now be followd by the last minute digit. E.g. 2:40, 2:53.
            secondDigit.addChild(minuteSecondDigit);

            // The first digit may be 3-9.
            firstDigit = new Node(k3, k4, k5, k6, k7, k8, k9);
            mLegalTimesTree.addChild(firstDigit);
            // We must now be followed by the first minute digit. E.g. 3:57, 8:12.
            firstDigit.addChild(minuteFirstDigit);
        } else {
            // We'll need to use the AM/PM node a lot.
            // Set up AM and PM to respond to "a" and "p".
            Node ampm = new Node(getAmOrPmKeyCode(HALF_DAY_1), getAmOrPmKeyCode(HALF_DAY_2));

            // The first hour digit may be 1.
            Node firstDigit = new Node(k1);
            mLegalTimesTree.addChild(firstDigit);
            // We'll allow quick input of on-the-hour times. E.g. 1pm.
            firstDigit.addChild(ampm);

            // When the first digit is 1, the second digit may be 0-2.
            Node secondDigit = new Node(k0, k1, k2);
            firstDigit.addChild(secondDigit);
            // Also for quick input of on-the-hour times. E.g. 10pm, 12am.
            secondDigit.addChild(ampm);

            // When the first digit is 1, and the second digit is 0-2, the third digit may be 0-5.
            Node thirdDigit = new Node(k0, k1, k2, k3, k4, k5);
            secondDigit.addChild(thirdDigit);
            // The time may be finished now. E.g. 1:02pm, 1:25am.
            thirdDigit.addChild(ampm);

            // When the first digit is 1, the second digit is 0-2, and the third digit is 0-5,
            // the fourth digit may be 0-9.
            Node fourthDigit = new Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9);
            thirdDigit.addChild(fourthDigit);
            // The time must be finished now. E.g. 10:49am, 12:40pm.
            fourthDigit.addChild(ampm);

            // When the first digit is 1, and the second digit is 0-2, the third digit may be 6-9.
            thirdDigit = new Node(k6, k7, k8, k9);
            secondDigit.addChild(thirdDigit);
            // The time must be finished now. E.g. 1:08am, 1:26pm.
            thirdDigit.addChild(ampm);

            // When the first digit is 1, the second digit may be 3-5.
            secondDigit = new Node(k3, k4, k5);
            firstDigit.addChild(secondDigit);

            // When the first digit is 1, and the second digit is 3-5, the third digit may be 0-9.
            thirdDigit = new Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9);
            secondDigit.addChild(thirdDigit);
            // The time must be finished now. E.g. 1:39am, 1:50pm.
            thirdDigit.addChild(ampm);

            // The hour digit may be 2-9.
            firstDigit = new Node(k2, k3, k4, k5, k6, k7, k8, k9);
            mLegalTimesTree.addChild(firstDigit);
            // We'll allow quick input of on-the-hour-times. E.g. 2am, 5pm.
            firstDigit.addChild(ampm);

            // When the first digit is 2-9, the second digit may be 0-5.
            secondDigit = new Node(k0, k1, k2, k3, k4, k5);
            firstDigit.addChild(secondDigit);

            // When the first digit is 2-9, and the second digit is 0-5, the third digit may be 0-9.
            thirdDigit = new Node(k0, k1, k2, k3, k4, k5, k6, k7, k8, k9);
            secondDigit.addChild(thirdDigit);
            // The time must be finished now. E.g. 2:57am, 9:30pm.
            thirdDigit.addChild(ampm);
        }
    }
    
    public static final class Builder extends BottomSheetTimePickerDialog.Builder {
        private final int mHour;
        private final int mMinute;

        private int mHeaderTextColorSelected;
        private int mHeaderTextColorUnselected;
        private int mTimeSeparatorColor;
        private int mHalfDayButtonColorSelected;
        private int mHalfDayButtonColorUnselected;

        /**
         * @param listener      How the parent is notified that the time is set.
         * @param hourOfDay     The initial hour-of-day of the dialog.
         * @param minute        The initial minute of the dialog.
         * @param is24HourMode  Whether the dialog should be configured for 24-hour mode.
         */
        public Builder(OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourMode) {
            super(listener, is24HourMode);
            mHour = hourOfDay;
            mMinute = minute;
        }

        /**
         * Set the color of the header text when it is selected.
         */
        public Builder setHeaderTextColorSelected(@ColorInt int color) {
            mHeaderTextColorSelected = color;
            return this;
        }

        /**
         * Set the color of the header text when it is not selected.
         */
        public Builder setHeaderTextColorUnselected(@ColorInt int color) {
            mHeaderTextColorUnselected = color;
            return this;
        }

        /**
         * Set the color of the time separator that separates the hour and minute views in the header.
         */
        public Builder setTimeSeparatorColor(@ColorInt int color) {
            mTimeSeparatorColor = color;
            return this;
        }

        /**
         * Set the color of the AM/PM text when it is selected. This is equivalent to
         * {@link #setHalfDayButtonColorSelected(int)} in 24-hour time.
         */
        public Builder setAmPmTextColorSelected(@ColorInt int color) {
            setHalfDayButtonColorSelected(color);
            return this;
        }

        /**
         * Set the color of the AM/PM text when it is not selected. This is equivalent to
         * {@link #setHalfDayButtonColorUnselected(int)} in 24-hour time.
         */
        public Builder setAmPmTextColorUnselected(@ColorInt int color) {
            setHalfDayButtonColorUnselected(color);
            return this;
        }

        /**
         * Set the color of the half-day image button when it is selected. This is equivalent to
         * {@link #setAmPmTextColorSelected(int)} in 12-hour time.
         */
        public Builder setHalfDayButtonColorSelected(@ColorInt int color) {
            mHalfDayButtonColorSelected = color;
            return this;
        }

        /**
         * Set the color of the half-day image button when it is not selected. This is equivalent to
         * {@link #setAmPmTextColorUnselected(int)} in 12-hour time.
         */
        public Builder setHalfDayButtonColorUnselected(@ColorInt int color) {
            mHalfDayButtonColorUnselected = color;
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
        public GridTimePickerDialog build() {
            GridTimePickerDialog dialog = newInstance(mListener, mHour, mMinute, mIs24HourMode);
            super_build(dialog);
            dialog.setHeaderTextColorSelected(mHeaderTextColorSelected);
            dialog.setHeaderTextColorUnselected(mHeaderTextColorUnselected);
            dialog.setHalfDayButtonColorSelected(mHalfDayButtonColorSelected);
            dialog.setHalfDayButtonColorUnselected(mHalfDayButtonColorUnselected);
            dialog.setTimeSeparatorColor(mTimeSeparatorColor);
            return dialog;
        }
    }

    /**
     * Simple node class to be used for traversal to check for legal times.
     * mLegalKeys represents the keys that can be typed to get to the node.
     * mChildren are the children that can be reached from this node.
     */
    private class Node {
        private int[] mLegalKeys;
        private ArrayList<Node> mChildren;

        public Node(int... legalKeys) {
            mLegalKeys = legalKeys;
            mChildren = new ArrayList<Node>();
        }

        public void addChild(Node child) {
            mChildren.add(child);
        }

        public boolean containsKey(int key) {
            for (int i = 0; i < mLegalKeys.length; i++) {
                if (mLegalKeys[i] == key) {
                    return true;
                }
            }
            return false;
        }

        public Node canReach(int key) {
            if (mChildren == null) {
                return null;
            }
            for (Node child : mChildren) {
                if (child.containsKey(key)) {
                    return child;
                }
            }
            return null;
        }
    }

    private class KeyboardListener implements View.OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                return processKeyUp(keyCode);
            }
            return false;
        }
    }
}
