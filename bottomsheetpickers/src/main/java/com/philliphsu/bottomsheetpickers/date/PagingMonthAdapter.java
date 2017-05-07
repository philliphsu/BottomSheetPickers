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

package com.philliphsu.bottomsheetpickers.date;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.philliphsu.bottomsheetpickers.Utils;
import com.philliphsu.bottomsheetpickers.date.MonthView.OnDayClickListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * An adapter for a pager of {@link MonthView} items.
 */
class PagingMonthAdapter extends PagerAdapter implements OnDayClickListener {
    private static final String TAG = "SimpleMonthAdapter";

    private static final String KEY_POSITIONS = "positions";
    private static final String KEY_MONTH_YEAR_TITLES = "month_year_titles";

    private final Context mContext;
    private final boolean mThemeDark;
    private final int mAccentColor;
    private final SparseArray<String> mMonthYearTitles = new SparseArray<>();
    private final ArrayList<MonthView> mMonthViews = new ArrayList<>();
    protected final DatePickerController mController;

    private CalendarDay mSelectedDay;
    private MonthView mCurrentPrimaryItem;

    protected static int WEEK_7_OVERHANG_HEIGHT = 7;
    protected static final int MONTHS_IN_YEAR = 12;

    public PagingMonthAdapter(Context context,
                        DatePickerController controller) {
        this(context, controller, false);
    }

    public PagingMonthAdapter(Context context,
                        DatePickerController controller,
                        boolean themeDark) {
        this(context, controller, themeDark, Utils.getThemeAccentColor(context));
    }

    public PagingMonthAdapter(Context context, DatePickerController controller,
                              boolean themeDark, int accentColor) {
        mContext = context;
        mController = controller;
        mThemeDark = themeDark;
        mAccentColor = accentColor;
        init();
        setSelectedDay(mController.getSelectedDay());
    }

    /**
     * Updates the selected day and related parameters.
     *
     * @param day The day to highlight
     */
    public void setSelectedDay(CalendarDay day) {
        mSelectedDay = day;
        notifyDataSetChanged(); // This won't refresh our views!
        // In the ListView-based day picker, notifyDataSetChanged() would have refreshed this
        // adapter's contents, so instantiateItem() is called again on the entire data set.
        // This would have called each MonthView's setMonthParams() with the correct value for the
        // selectedDay (either -1 or the actual selected day if the selection is in that month)
        // and invalidate(). However, PagerAdapter's implementation of notifyDataSetChanged()
        // won't do all this for us, so we have to manually do it.
        for (MonthView mv : mMonthViews) {
            mv.setSelectedDay(mv == mCurrentPrimaryItem ? day.day : -1);
            mv.invalidate();
        }
    }

    public CalendarDay getSelectedDay() {
        return mSelectedDay;
    }

    /**
     * Set up the gesture detector and selected time
     */
    protected void init() {
        mSelectedDay = new CalendarDay(System.currentTimeMillis());
    }

    @Override
    public int getCount() {
        // Add 1 to get the total number of years in the year range.
        // e.g. There are 11 years in the range [2016, 2026].
        return (mController.getMaxYear() - mController.getMinYear() + 1) * MONTHS_IN_YEAR;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MonthView v;
        HashMap<String, Integer> drawingParams = null;
        v = createMonthView(mContext, mThemeDark, mAccentColor);
        // Set up the new view
        v.setClickable(true);
        v.setOnDayClickListener(this);
        if (drawingParams == null) {
            drawingParams = new HashMap<>();
        }
        drawingParams.clear();

        final int month = getMonth(position);
        final int year = getYear(position);

        int selectedDay = -1;
        if (isSelectedDayInMonth(year, month)) {
            selectedDay = mSelectedDay.day;
        }

        // Invokes requestLayout() to ensure that the recycled view is set with the appropriate
        // height/number of weeks before being displayed.
        v.reuse();

        drawingParams.put(MonthView.VIEW_PARAMS_SELECTED_DAY, selectedDay);
        drawingParams.put(MonthView.VIEW_PARAMS_YEAR, year);
        drawingParams.put(MonthView.VIEW_PARAMS_MONTH, month);
        drawingParams.put(MonthView.VIEW_PARAMS_WEEK_START, mController.getFirstDayOfWeek());
        v.setMonthParams(drawingParams);
        v.invalidate();
        container.addView(v, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mMonthYearTitles.append(position, v.getMonthAndYearString());
        mMonthViews.add(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mMonthYearTitles.delete(position);
        mMonthViews.remove(object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentPrimaryItem = (MonthView) object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mMonthYearTitles.get(position);
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        final int size = mMonthYearTitles.size();
        if (size > 0) {
            state = new Bundle();
            String[] titles = new String[size];
            int[] positions = new int[size];
            for (int i = 0; i < size; i++) {
                titles[i] = mMonthYearTitles.valueAt(i);
                positions[i] = mMonthYearTitles.keyAt(i);
            }
            state.putStringArray(KEY_MONTH_YEAR_TITLES, titles);
            state.putIntArray(KEY_POSITIONS, positions);
        }
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle)state;
            bundle.setClassLoader(loader);
            String[] titles = bundle.getStringArray(KEY_MONTH_YEAR_TITLES);
            int[] positions = bundle.getIntArray(KEY_POSITIONS);
            mMonthYearTitles.clear();
            if (titles != null && positions != null) {
                // Both arrays should be the same size, so use either length.
                for (int i = 0; i < titles.length; i++) {
                    mMonthYearTitles.append(positions[i], titles[i]);
                }
            }
        }
    }

    public MonthView createMonthView(Context context) {
        return createMonthView(context, false);
    }

    public MonthView createMonthView(Context context, boolean themeDark) {
        return createMonthView(context, themeDark, mAccentColor);
    }

    public MonthView createMonthView(Context context, boolean themeDark, int accentColor) {
        final MonthView monthView = new SimpleMonthView(context);
        monthView.setDatePickerController(mController);
        monthView.setTheme(context, themeDark);
        monthView.setTodayNumberColor(accentColor);
        monthView.setSelectedCirclePaintColor(accentColor);
        return monthView;
    }

    private boolean isSelectedDayInMonth(int year, int month) {
        return mSelectedDay.year == year && mSelectedDay.month == month;
    }

    @Override
    public void onDayClick(MonthView view, CalendarDay day) {
        if (day != null) {
            onDayTapped(day);
        }
    }

    /**
     * Maintains the same hour/min/sec but moves the day to the tapped day.
     *
     * @param day The day that was tapped
     */
    protected void onDayTapped(CalendarDay day) {
        mController.tryVibrate();
        mController.onDayOfMonthSelected(day.year, day.month, day.day);
        setSelectedDay(day);
    }

    /**
     * Returns the month (0 - 11) displayed at this position.
     */
    final int getMonth(int position) {
        return position % MONTHS_IN_YEAR;
    }

    /**
     * Returns the year displayed at this position.
     */
    final int getYear(int position) {
        return position / MONTHS_IN_YEAR + mController.getMinYear();
    }

    /**
     * @return The page position at which the given day is located.
     */
    final int getPosition(CalendarDay day) {
        int positionOfYear = MONTHS_IN_YEAR * (day.year - mController.getMinYear());
        return positionOfYear + day.month;
    }
}
