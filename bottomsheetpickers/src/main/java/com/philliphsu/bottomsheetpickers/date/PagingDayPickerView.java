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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.Utils;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog.OnDateChangedListener;
import com.philliphsu.bottomsheetpickers.date.MonthPickerView.OnMonthClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.support.v4.content.ContextCompat.getColor;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * This displays a ViewPager of months in a calendar format with selectable days.
 */
class PagingDayPickerView extends LinearLayout implements OnDateChangedListener, OnPageChangeListener, OnMonthClickListener {

    private static final String TAG = "MonthFragment";

    static final int DAY_PICKER_INDEX = 0;
    static final int MONTH_PICKER_INDEX = 1;

    static int MONTH_NAVIGATION_BAR_SIZE;

    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());

    protected Handler mHandler;

    // highlighted time
    protected CalendarDay mSelectedDay = new CalendarDay();
    protected PagingMonthAdapter mAdapter;

    private DayPickerViewAnimator mMonthAnimator;
    private ViewPager mViewPager;
    private MonthPickerView mMonthPickerView;
    private TextView mMonthYearTitleView;
    private ImageButton mPreviousButton;
    private ImageButton mNextButton;
    private View mTitleContainer;

    // The arrow that initially points down and rotates to point up
    private AnimatedVectorDrawableCompat mArrowDownDrawable;
    // The arrow that initially points up and rotates to point down
    private AnimatedVectorDrawableCompat mArrowUpDrawable;

    protected CalendarDay mTempDay = new CalendarDay();

    // which month should be displayed/highlighted [0-11]
    protected int mCurrentMonthDisplayed;
    // The currently displayed view
    private int mCurrentView = DAY_PICKER_INDEX;
    // The year associated with the current MonthView displayed
    private int mCurrentYearDisplayed;

    private DatePickerController mController;

    private boolean mThemeDark;
    private int mAccentColor;

    public PagingDayPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PagingDayPickerView(Context context, DatePickerController controller) {
        this(context, controller, false);
    }

    public PagingDayPickerView(Context context, DatePickerController controller, boolean themeDark) {
        this(context, controller, themeDark, Utils.getThemeAccentColor(context));
    }

    public PagingDayPickerView(Context context, DatePickerController controller, boolean themeDark,
                               int accentColor) {
        super(context);
        // keep these before init()
        mThemeDark = themeDark;
        mAccentColor = accentColor;
        init(context);
        setController(controller);
    }

    public void setController(DatePickerController controller) {
        mController = controller;
        mController.registerOnDateChangedListener(this);
        refreshAdapter();
        onDateChanged();
        mMonthPickerView.setDatePickerController(mController);
    }

    private void init(Context context) {
        mHandler = new Handler();
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));

        Resources res = getResources();
        MONTH_NAVIGATION_BAR_SIZE = res.getDimensionPixelOffset(R.dimen.bsp_month_navigation_bar_height)
                + res.getDimensionPixelOffset(R.dimen.bsp_month_view_top_padding);

        final View view = LayoutInflater.from(context).inflate(R.layout.bsp_day_picker_content, this, true);
        mMonthAnimator = (DayPickerViewAnimator) findViewById(R.id.bsp_month_animator);
        mMonthPickerView = (MonthPickerView) findViewById(R.id.bsp_month_picker);
        mMonthPickerView.setOnMonthClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.bsp_viewpager);
        mViewPager.addOnPageChangeListener(this);
        mMonthYearTitleView = (TextView) view.findViewById(R.id.bsp_month_year_title);
        mTitleContainer = view.findViewById(R.id.bsp_month_year_title_container);
        mTitleContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int newIndex = mCurrentView == DAY_PICKER_INDEX ? MONTH_PICKER_INDEX : DAY_PICKER_INDEX;
                setupCurrentView(newIndex, true);
            }
        });
        mPreviousButton = (ImageButton) view.findViewById(R.id.bsp_prev);
        mPreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousItem = mViewPager.getCurrentItem() - 1;
                if (previousItem >= 0) {
                    mViewPager.setCurrentItem(previousItem, true);
                }
            }
        });
        mNextButton = (ImageButton) view.findViewById(R.id.bsp_next);
        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextItem = mViewPager.getCurrentItem() + 1;
                if (nextItem < mAdapter.getCount()) {
                    mViewPager.setCurrentItem(nextItem, true);
                }
            }
        });

        mArrowDownDrawable = AnimatedVectorDrawableCompat.create(context, R.drawable.bsp_animated_arrow_drop_down);
        mArrowUpDrawable   = AnimatedVectorDrawableCompat.create(context, R.drawable.bsp_animated_arrow_drop_up);
        setArrowDrawableOnTitle(mArrowDownDrawable);

        // Theme-specific configurations.
        if (mThemeDark) {
            int selectableItemBg = getColor(context, R.color.bsp_selectable_item_background_dark);
            Utils.setColorControlHighlight(mPreviousButton, selectableItemBg);
            Utils.setColorControlHighlight(mNextButton, selectableItemBg);
            Utils.setColorControlHighlight(mTitleContainer, selectableItemBg);
            int cursor = getColor(context, R.color.bsp_text_color_secondary_dark);
            Utils.applyTint(mPreviousButton, cursor);
            Utils.applyTint(mNextButton, cursor);
        }

        // Set up colors.
        int monthYearTitleColor = getColor(context, mThemeDark?
                R.color.bsp_text_color_primary_dark : R.color.bsp_text_color_primary_light);
        int dropdownArrowColor = getColor(context, mThemeDark?
                R.color.bsp_icon_color_active_dark : R.color.bsp_icon_color_active_light);

        mMonthYearTitleView.setTextColor(monthYearTitleColor);
        mArrowDownDrawable.setTint(dropdownArrowColor);
        mArrowUpDrawable.setTint(dropdownArrowColor);

        mMonthPickerView.setTheme(context, mThemeDark);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mViewPager.removeOnPageChangeListener(this);
    }

    void setTheme(Context context, boolean themeDark) {
        mThemeDark = themeDark;
    }

    void setAccentColor(@ColorInt int color) {
        mAccentColor = color;
        mMonthPickerView.setCurrentMonthTextColor(color);
        mMonthPickerView.setSelectedCirclePaintColor(color);
    }

    public void onChange() {
        refreshAdapter();
        refreshMonthPicker();
    }

    /**
     * Handles everything related to setting up the current view.
     */
    void setupCurrentView(int currentView, boolean animate) {
        if (currentView == DAY_PICKER_INDEX || currentView == MONTH_PICKER_INDEX) {
            boolean isDayPicker = currentView == DAY_PICKER_INDEX;
            setCurrentView(currentView, animate);
            if (isDayPicker) {
                setTitle(mAdapter.getPageTitle(mViewPager.getCurrentItem()));
                toggleArrowsVisibility(getPagerPosition());
            } else {
                // Fortunately, very few locales have a year pattern string different
                // from "yyyy". Localization isn't too important here.
                // TODO: Decide if you really want the year to be localized.
                setTitle(String.valueOf(mCurrentYearDisplayed));
                toggleArrowsVisibility(false, false);
            }
        } else {
            Log.e(TAG, "Error restoring current view");
        }
    }

    /**
     * Creates a new adapter if necessary and sets up its parameters. Override
     * this method to provide a custom adapter.
     */
    protected void refreshAdapter() {
        if (mAdapter == null) {
            if (mAccentColor != 0) {
                mAdapter = createMonthAdapter(getContext(), mController, mThemeDark, mAccentColor);
            } else {
                mAdapter = createMonthAdapter(getContext(), mController, mThemeDark);
            }
        } else {
            mAdapter.setSelectedDay(mSelectedDay);
        }
        // refresh the view with the new parameters
        mViewPager.setAdapter(mAdapter);
    }

    /**
     * Updates the month picker's display parameters with the selected
     * date from the date picker.
     */
    private void refreshMonthPicker() {
        prepareMonthPickerForDisplay(mSelectedDay.year);
        mMonthPickerView.invalidate();
    }

    public PagingMonthAdapter createMonthAdapter(Context context,
                                                 DatePickerController controller) {
        return new PagingMonthAdapter(context, controller);
    }

    public PagingMonthAdapter createMonthAdapter(Context context,
                                                 DatePickerController controller,
                                                 boolean themeDark) {
        return createMonthAdapter(context, controller, themeDark, Utils.getThemeAccentColor(context));
    }

    public PagingMonthAdapter createMonthAdapter(Context context,
                                                 DatePickerController controller,
                                                 boolean themeDark,
                                                 int accentColor) {
        return new PagingMonthAdapter(context, controller, themeDark, accentColor);
    }

    /**
     * This moves to the specified time in the view. If the time is not already
     * in range it will move the list so that the first of the month containing
     * the time is at the top of the view. If the new time is already in view
     * the list will not be scrolled unless forceScroll is true. This time may
     * optionally be highlighted as selected as well.
     *
     * @param day The time to move to
     * @param animate Whether to scroll to the given time or just redraw at the
     *            new location
     * @param setSelected Whether to set the given time as selected
     * @param forceScroll Whether to recenter even if the time is already
     *            visible
     * @return Whether or not the view animated to the new location
     */
    public boolean goTo(CalendarDay day, boolean animate, boolean setSelected, boolean forceScroll) {
        final int selectedPosition = getPosition(mSelectedDay);

        // Set the selected day
        if (setSelected) {
            mSelectedDay.set(day);
        }

        mTempDay.set(day);
        final int position = getPosition(day);

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "GoTo position " + position);
        }
        // Check if the selected day is now outside of our visible range
        // and if so scroll to the month that contains it
        if (position != selectedPosition || forceScroll) {
            setMonthAndYearDisplayed(mTempDay);
            if (animate) {
                mViewPager.setCurrentItem(position, true);
                if (setSelected) {
                    setSelectedDay(mSelectedDay);
                }
                return true;
            } else {
                postSetSelection(position, setSelected);
            }
        } else if (setSelected) {
            setMonthAndYearDisplayed(mSelectedDay);
            setSelectedDay(mSelectedDay);
        }
        return false;
    }

    /**
     * @return The page position at which the given day is located.
     */
    private int getPosition(CalendarDay day) {
        return mAdapter.getPosition(day);
    }

    public void postSetSelection(final int position, final boolean setSelected) {
        clearFocus();
        post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(position, false);
                if (setSelected) {
                    setSelectedDay(mSelectedDay);
                }
            }
        });
    }

    void postSetupCurrentView(final int currentView, final boolean animate) {
        post(new Runnable() {
            @Override
            public void run() {
                setupCurrentView(currentView, animate);
            }
        });
    }

    /**
     * Sets the month and year displayed at the top of this view based on time.
     * Override to add custom events when the title is changed.
     */
    protected void setMonthAndYearDisplayed(CalendarDay date) {
        mCurrentMonthDisplayed = date.month;
        mCurrentYearDisplayed = date.year;
    }

    private void setSelectedDay(CalendarDay day) {
        mAdapter.setSelectedDay(day);
    }

    @Override
    public void onDateChanged() {
        if (mCurrentView != DAY_PICKER_INDEX) {
            setCurrentView(DAY_PICKER_INDEX, false);
            // Restore the title and cursors
            onPageSelected(mViewPager.getCurrentItem());
        }
        goTo(mController.getSelectedDay(), false, true, true);
    }

    /**
     * Attempts to return the date that has accessibility focus.
     *
     * @return The date that has accessibility focus, or {@code null} if no date
     *         has focus.
     */
    private CalendarDay findAccessibilityFocus() {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child instanceof MonthView) {
                final CalendarDay focus = ((MonthView) child).getAccessibilityFocus();
                if (focus != null) {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        // Clear focus to avoid ListView bug in Jelly Bean MR1.
                        ((MonthView) child).clearAccessibilityFocus();
                    }
                    return focus;
                }
            }
        }

        return null;
    }

    /**
     * Attempts to restore accessibility focus to a given date. No-op if
     * {@code day} is {@code null}.
     *
     * @param day The date that should receive accessibility focus
     * @return {@code true} if focus was restored
     */
    private boolean restoreAccessibilityFocus(CalendarDay day) {
        if (day == null) {
            return false;
        }

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child instanceof MonthView) {
                if (((MonthView) child).restoreAccessibilityFocus(day)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setItemCount(-1);
   }

    private static String getMonthAndYearString(CalendarDay day) {
        Calendar cal = Calendar.getInstance();
        cal.set(day.year, day.month, day.day);

        StringBuffer sbuf = new StringBuffer();
        sbuf.append(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        sbuf.append(" ");
        sbuf.append(YEAR_FORMAT.format(cal.getTime()));
        return sbuf.toString();
    }

    /**
     * Necessary for accessibility, to ensure we support "scrolling" forward and backward
     * in the month list.
     */
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
      super.onInitializeAccessibilityNodeInfo(info);
      info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
      info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }

    /**
     * When scroll forward/backward events are received, announce the newly scrolled-to month.
     */
    @SuppressLint("NewApi")
    @Override
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (action != AccessibilityNodeInfo.ACTION_SCROLL_FORWARD &&
                action != AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) {
            return super.performAccessibilityAction(action, arguments);
        }

        // Figure out what month is showing.
        int firstVisiblePosition = getPagerPosition();
        int month = firstVisiblePosition % 12;
        int year = firstVisiblePosition / 12 + mController.getMinYear();
        CalendarDay day = new CalendarDay(year, month, 1);

        // Scroll either forward or backward one month.
        if (action == AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) {
            day.month++;
            if (day.month == 12) {
                day.month = 0;
                day.year++;
            }
        } else if (action == AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) {
            View firstVisibleView = getChildAt(0);
            // If the view is fully visible, jump one month back. Otherwise, we'll just jump
            // to the first day of first visible month.
            if (firstVisibleView != null && firstVisibleView.getTop() >= -1) {
                // There's an off-by-one somewhere, so the top of the first visible item will
                // actually be -1 when it's at the exact top.
                day.month--;
                if (day.month == -1) {
                    day.month = 11;
                    day.year--;
                }
            }
        }

        // Go to that month.
        Utils.tryAccessibilityAnnounce(this, getMonthAndYearString(day));
        goTo(day, true, false, true);
        return true;
    }

    int getPagerPosition() {
        return mViewPager.getCurrentItem();
    }

    int getCurrentView() {
        return mCurrentView;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mCurrentView == DAY_PICKER_INDEX) {
            setTitle(mAdapter.getPageTitle(position));
            toggleArrowsVisibility(position);
            final int month = mAdapter.getMonth(position);
            final int year = mAdapter.getYear(position);
            if (mCurrentYearDisplayed != year) {
                mCurrentYearDisplayed = year;
            }
            if (mCurrentMonthDisplayed != month) {
                mCurrentMonthDisplayed = month;
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setTitle(CharSequence title) {
        mMonthYearTitleView.setText(title);
    }

    /**
     * A variant of {@link #toggleArrowsVisibility(boolean, boolean)} suitable
     * for when a new page has been selected.
     *
     * @param position The page position
     */
    private void toggleArrowsVisibility(int position) {
        toggleArrowsVisibility(position > 0, position + 1 < mAdapter.getCount());
    }

    private void toggleArrowsVisibility(boolean leftVisible, boolean rightVisible) {
        mPreviousButton.setVisibility(leftVisible ? VISIBLE : INVISIBLE);
        mNextButton.setVisibility(rightVisible ? VISIBLE : INVISIBLE);
    }

    private void setArrowDrawableOnTitle(@NonNull Drawable arrow) {
        if (Utils.checkApiLevel(17)) {
            mMonthYearTitleView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, arrow, null);
        } else {
            mMonthYearTitleView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrow, null);
        }
    }

    private void animateArrow(AnimatedVectorDrawableCompat arrow) {
        setArrowDrawableOnTitle(arrow);
        arrow.start();
    }

    private void setCurrentView(final int viewIndex, boolean animate) {
        switch (viewIndex) {
            case DAY_PICKER_INDEX:
                if (mCurrentView != viewIndex) {
                    mMonthAnimator.setDisplayedChild(DAY_PICKER_INDEX, animate);
                    animateArrow(mArrowUpDrawable);
                    mCurrentView = viewIndex;
                }
                break;
            case MONTH_PICKER_INDEX:
                if (mCurrentView != viewIndex) {
                    prepareMonthPickerForDisplay(mCurrentYearDisplayed);
                    mMonthAnimator.setDisplayedChild(MONTH_PICKER_INDEX, animate);
                    animateArrow(mArrowDownDrawable);
                    mCurrentView = viewIndex;
                }
                break;
        }
    }

    private void prepareMonthPickerForDisplay(int currentYear) {
        mMonthPickerView.setDisplayParams(mSelectedDay, currentYear);
    }

    @Override
    public void onMonthClick(MonthPickerView view, int month, int year) {
        // This needs to be called before we call back to onMonthYearSelected().
        // Otherwise, our listener will call our onDateChanged(), in which
        // setCurrentView() is called with the 'animate' parameter set to false.
        // This next call would subsequently not call through, since the current
        // index will have already been set to DAY_PICKER_INDEX.
        setCurrentView(DAY_PICKER_INDEX, true);
        // If the same month was selected, onPageSelected() won't automatically
        // call through because we're staying on the same page.
        // This must be called before everything else, ESPECIALLY for the
        // scenario where a different month was selected! Otherwise, setTitle()
        // ends up being called twice and you'll notice "stuttering".
        //
        // This happens because mController reacts by calling our onDateChanged(),
        // which calls our goTo(), where it updates the value of mCurrentMonthDisplayed.
        // Since the pages will change, onPageSelected() will call through and one
        // call to setTitle() is made. After that, one more call would be made from here.
        if (month == mCurrentMonthDisplayed) {
            // manual invocation
            onPageSelected(mViewPager.getCurrentItem());
        }
        mController.tryVibrate();
        mController.onMonthYearSelected(month, year);
    }
}
