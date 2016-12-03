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
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.philliphsu.bottomsheetpickers.date.MonthAdapter.CalendarDay;
import com.philliphsu.bottomsheetpickers.date.MonthView.OnDayClickListener;

import java.util.HashMap;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * An adapter for a pager of {@link MonthView} items.
 */
class PagingMonthAdapter extends PagerAdapter implements OnDayClickListener {
    private static final String TAG = "SimpleMonthAdapter";

    private final Context mContext;
    protected final DatePickerController mController;
    private final boolean mThemeDark;
    private final SparseArray<MonthView> mMonthViews = new SparseArray<>();

    private CalendarDay mSelectedDay;

    protected static int WEEK_7_OVERHANG_HEIGHT = 7;
    protected static final int MONTHS_IN_YEAR = 12;

    public PagingMonthAdapter(Context context,
                        DatePickerController controller) {
        this(context, controller, false);
    }

    public PagingMonthAdapter(Context context,
                        DatePickerController controller,
                        boolean themeDark) {
        mContext = context;
        mController = controller;
        mThemeDark = themeDark;
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
        notifyDataSetChanged();
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
        return ((mController.getMaxYear() - mController.getMinYear()) + 1) * MONTHS_IN_YEAR;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MonthView v;
        HashMap<String, Integer> drawingParams = null;
        v = createMonthView(mContext, mThemeDark);
        // Set up the new view
        v.setClickable(true);
        v.setOnDayClickListener(this);
        if (drawingParams == null) {
            drawingParams = new HashMap<>();
        }
        drawingParams.clear();

        final int month = position % MONTHS_IN_YEAR;
        final int year = position / MONTHS_IN_YEAR + mController.getMinYear();

        int selectedDay = -1;
        if (isSelectedDayInMonth(year, month)) {
            selectedDay = mSelectedDay.day;
        }

        // This method is instantiateItem(), so there is no recycling going on.
        // TODO: Verify that we can delete this.
//        // Invokes requestLayout() to ensure that the recycled view is set with the appropriate
//        // height/number of weeks before being displayed.
//        v.reuse();

        drawingParams.put(MonthView.VIEW_PARAMS_SELECTED_DAY, selectedDay);
        drawingParams.put(MonthView.VIEW_PARAMS_YEAR, year);
        drawingParams.put(MonthView.VIEW_PARAMS_MONTH, month);
        drawingParams.put(MonthView.VIEW_PARAMS_WEEK_START, mController.getFirstDayOfWeek());
        v.setMonthParams(drawingParams);
        v.invalidate();
        container.addView(v, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mMonthViews.append(position, v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mMonthViews.delete(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        MonthView mv = mMonthViews.get(position);
        return mv != null ? mv.getMonthAndYearString() : null;
    }

    public MonthView createMonthView(Context context) {
        return createMonthView(context, false);
    }

    public MonthView createMonthView(Context context, boolean themeDark) {
        final MonthView monthView = new SimpleMonthView(context);
        monthView.setDatePickerController(mController);
        monthView.setTheme(context, themeDark);
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
}
