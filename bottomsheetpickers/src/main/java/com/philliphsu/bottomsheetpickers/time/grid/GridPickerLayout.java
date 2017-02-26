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
 * limitations under the License.
 */

package com.philliphsu.bottomsheetpickers.time.grid;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ViewAnimator;

import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.Utils;

/**
 * A derivative of the AOSP datetimepicker RadialPickerLayout class.
 * The animations used here are taken from the DatePickerDialog class.
 */
// TODO: Accessibility related code was stripped. Restore?
public class GridPickerLayout extends ViewAnimator implements NumbersGrid.OnNumberSelectedListener {
    private static final String TAG = "GridSelectorLayout";

    private static final int ANIMATION_DURATION = 300;
    private static final int HOUR_INDEX         = GridTimePickerDialog.HOUR_INDEX;
    private static final int MINUTE_INDEX       = GridTimePickerDialog.MINUTE_INDEX;
    private static final int HALF_DAY_INDEX     = GridTimePickerDialog.HALF_DAY_INDEX;
    private static final int HALF_DAY_1         = GridTimePickerDialog.HALF_DAY_1;
    private static final int HALF_DAY_2         = GridTimePickerDialog.HALF_DAY_2;

    private OnValueSelectedListener mListener;
    private boolean mTimeInitialized;
    private int mCurrentHoursOfDay;
    private int mCurrentMinutes;
    private boolean mIs24HourMode;
    private int mCurrentItemShowing;

    private HoursGrid mHoursGrid;
    private TwentyFourHoursGrid m24HoursGrid;
    private MinutesGrid mMinutesGrid;

    private final Animation mInAnimation;
    private final Animation mOutAnimation;

    public interface OnValueSelectedListener {
        void onValueSelected(int pickerIndex, int newValue, boolean autoAdvance);
    }

    public GridPickerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInAnimation = new AlphaAnimation(0.0f, 1.0f);
        mInAnimation.setDuration(ANIMATION_DURATION);
        mOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        mOutAnimation.setDuration(ANIMATION_DURATION);
    }

    public void initialize(Context context, int initialHoursOfDay, int initialMinutes, boolean is24HourMode) {
        if (mTimeInitialized) {
            Log.e(TAG, "Time has already been initialized.");
            return;
        }

        // *****************************************************************************************
        // TODO: Should we move this block to GridTimePickerDialog? It would be pretty similar
        // to what AOSP's DatePickerDialog does. I don't immediately see any
        // code that REALLY needs to be done in this class instead.
        mIs24HourMode = is24HourMode;
        if (is24HourMode) {
            m24HoursGrid = (TwentyFourHoursGrid) inflate(context, R.layout.bsp_pad_24_hours, null);
            m24HoursGrid.initialize(this/*OnNumberSelectedListener*/);
            if (initialHoursOfDay >= 12) {
                // 24 hour grid is always initialized with 00-11 in the primary position
                m24HoursGrid.swapTexts();
            }
            addView(m24HoursGrid);
        } else {
            mHoursGrid = (HoursGrid) inflate(context, R.layout.bsp_pad_12_hours, null);
            mHoursGrid.initialize(this/*OnNumberSelectedListener*/);
            addView(mHoursGrid);
        }
        mMinutesGrid = (MinutesGrid) inflate(context, R.layout.bsp_pad_minutes, null);
        mMinutesGrid.initialize(this/*OnNumberSelectedListener*/);
        addView(mMinutesGrid);

        setInAnimation(mInAnimation);
        setOutAnimation(mOutAnimation);

        // *****************************************************************************************

        // Initialize the currently-selected hour and minute.
        setValueForItem(HOUR_INDEX, initialHoursOfDay);
        setValueForItem(MINUTE_INDEX, initialMinutes);

        mTimeInitialized = true;
    }

    void setTheme(Context context, boolean themeDark) {
        if (m24HoursGrid != null) {
            m24HoursGrid.setTheme(context, themeDark);
        } else if (mHoursGrid != null) {
            mHoursGrid.setTheme(context, themeDark);
        }
        mMinutesGrid.setTheme(context, themeDark);
    }

    void setAccentColor(@ColorInt int color) {
        if (m24HoursGrid != null) {
            m24HoursGrid.setAccentColor(color);
        } else if (mHoursGrid != null) {
            mHoursGrid.setAccentColor(color);
        }
        mMinutesGrid.setAccentColor(color);

        // Set the current selections again so that the new color is displayed.
        // You will almost always call initialize() before this, or else all of the grid views will
        // be null, so perhaps checking if we have already been initialized is redundant.
        if (mTimeInitialized) {
            if (m24HoursGrid != null) {
                m24HoursGrid.setSelection(m24HoursGrid.getSelection());
            } else if (mHoursGrid != null) {
                mHoursGrid.setSelection(mHoursGrid.getSelection());
            }
            mMinutesGrid.setSelection(mMinutesGrid.getSelection());
        }
    }

    public void setTime(int hours, int minutes) {
        setValueForItem(HOUR_INDEX, hours);
        setValueForItem(MINUTE_INDEX, minutes);
    }

    public void setOnValueSelectedListener(OnValueSelectedListener listener) {
        mListener = listener;
    }

    /**
     * Get the item (hours or minutes) that is currently showing.
     */
    public int getCurrentItemShowing() {
        if (mCurrentItemShowing != HOUR_INDEX && mCurrentItemShowing != MINUTE_INDEX) {
            Log.e(TAG, "Current item showing was unfortunately set to "+mCurrentItemShowing);
            return -1;
        }
        return mCurrentItemShowing;
    }

    /**
     * Set either minutes or hours as showing.
     * @param animate True to animate the transition, false to show with no animation.
     */
    public void setCurrentItemShowing(int index, boolean animate) {
        if (index != HOUR_INDEX && index != MINUTE_INDEX) {
            Log.e(TAG, "TimePicker does not support view at index "+index);
            return;
        }

        // Only highlight on TVs
        if (Utils.isTv(getContext())) {
            if (index == MINUTE_INDEX) {
                findViewById(R.id.bsp_minute_0).requestFocus();
            } else if (index == HOUR_INDEX) {
                if (findViewById(R.id.bsp_hour_0_12) != null) {
                    findViewById(R.id.bsp_hour_0_12).requestFocus();
                } else {
                    findViewById(R.id.bsp_hour_1).requestFocus();
                }
            }
        }

        int lastIndex = getCurrentItemShowing();
        mCurrentItemShowing = index;

        if (index != lastIndex) {
            setInAnimation(animate? mInAnimation : null);
            setOutAnimation(animate? mOutAnimation : null);
            setDisplayedChild(index);
        }
    }

    @Override
    public void onNumberSelected(int number) {
        // This will be set to true if this event was caused by long clicking in a TwentyFourHoursGrid.
        boolean fakeHourItemShowing = false;

        if (getCurrentItemShowing() == HOUR_INDEX) {
            if (!mIs24HourMode) {
                // Change the value before passing it through the callback
                int amOrPm = getIsCurrentlyAmOrPm();
                if (amOrPm == HALF_DAY_1 && number == 12) {
                    number = 0;
                } else if (amOrPm == HALF_DAY_2 && number != 12) {
                    number += 12;
                }
            } else {
                // Check if we would be changing half-days with the new value.
                // This can happen if this selection occurred with a long click.
                if (mCurrentHoursOfDay < 12 && number >= 12 || mCurrentHoursOfDay >= 12 && number < 12) {
                    int newHalfDay = getIsCurrentlyAmOrPm() == HALF_DAY_1 ? HALF_DAY_2 : HALF_DAY_1;
                    // Update the half-day toggles states
                    mListener.onValueSelected(HALF_DAY_INDEX, newHalfDay, false);
                    // Advance the index prematurely to bypass the animation that would otherwise
                    // be forced on us if we let the listener autoAdvance us.
                    setCurrentItemShowing(MINUTE_INDEX, false/*animate?*/);
                    // We need to "trick" the listener to think we're still on HOUR_INDEX.
                    // When the listener gets the onValueSelected() callback,
                    // it needs to call our setCurrentItemShowing() with MINUTE_INDEX a second time,
                    // so it ends up doing nothing. (Recall that the new index must be different from the
                    // last index for setCurrentItemShowing() to actually change the current item
                    // showing.) This has the effect of "tricking" the listener to update its
                    // own states relevant to the HOUR_INDEX, without having it actually autoAdvance
                    // and forcing an animation on us.
                    fakeHourItemShowing = true;
                }
            }
        }

        final int currentItemShowing = fakeHourItemShowing? HOUR_INDEX : getCurrentItemShowing();

        setValueForItem(currentItemShowing, number);
        mListener.onValueSelected(currentItemShowing, number,
                true/*autoAdvance, not considered for MINUTE_INDEX*/);
    }

    public int getHours() {
        return mCurrentHoursOfDay;
    }

    public int getMinutes() {
        return mCurrentMinutes;
    }

    /**
     * If the hours are showing, return the current hour. If the minutes are showing, return the
     * current minute.
     */
    private int getCurrentlyShowingValue() {
        int currentIndex = getCurrentItemShowing();
        if (currentIndex == HOUR_INDEX) {
            return mCurrentHoursOfDay;
        } else if (currentIndex == MINUTE_INDEX) {
            return mCurrentMinutes;
        } else {
            return -1;
        }
    }

    public int getIsCurrentlyAmOrPm() {
        if (mCurrentHoursOfDay < 12) {
            return HALF_DAY_1;
        } else if (mCurrentHoursOfDay < 24) {
            return HALF_DAY_2;
        }
        return -1;
    }

    /**
     * Set the internal as either {@link #HALF_DAY_1} or {@link #HALF_DAY_2}.
     */
    public void setHalfDay(int halfDay) {
        final int initialHalfDay = getIsCurrentlyAmOrPm();
        setValueForItem(HALF_DAY_INDEX, halfDay);
        if (halfDay != initialHalfDay && mIs24HourMode && m24HoursGrid != null) {
            m24HoursGrid.swapTexts();
            mListener.onValueSelected(HOUR_INDEX, mCurrentHoursOfDay, false);
        }
    }

    /**
     * Set the internal value for the hour, minute, or AM/PM.
     */
    private void setValueForItem(int index, int value) {
        if (index == HOUR_INDEX) {
            mCurrentHoursOfDay = value;
            setHourGridSelection(value);
        } else if (index == MINUTE_INDEX){
            mCurrentMinutes = value;
            mMinutesGrid.setSelection(value);
        } else if (index == HALF_DAY_INDEX) {
            if (value == HALF_DAY_1) {
                mCurrentHoursOfDay = mCurrentHoursOfDay % 12;
            } else if (value == HALF_DAY_2) {
                mCurrentHoursOfDay = (mCurrentHoursOfDay % 12) + 12;
            }
            setHourGridSelection(mCurrentHoursOfDay);
        }
    }

    private void setHourGridSelection(int value) {
        if (mIs24HourMode) {
            m24HoursGrid.setSelection(value);
        } else {
            value = value % 12;
            if (value == 0) {
                value = 12;
            }
            mHoursGrid.setSelection(value);
        }
    }
}
