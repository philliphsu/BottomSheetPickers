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
import android.widget.ViewAnimator;

import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.Utils;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog.OnDateChangedListener;
import com.philliphsu.bottomsheetpickers.date.MonthAdapter.CalendarDay;
import com.philliphsu.bottomsheetpickers.date.MonthPickerView.OnMonthClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.support.v4.content.ContextCompat.getColor;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.philliphsu.bottomsheetpickers.date.PagingMonthAdapter.MONTHS_IN_YEAR;

/**
 * This displays a ViewPager of months in a calendar format with selectable days.
 */
class PagingDayPickerView extends LinearLayout implements OnDateChangedListener, OnPageChangeListener, OnMonthClickListener {

    private static final String TAG = "MonthFragment";

    static final int DAY_PICKER_INDEX = 0;
    static final int MONTH_PICKER_INDEX = 1;

    // TODO: Delete, related to LIstView.
//    // Affects when the month selection will change while scrolling up
//    protected static final int SCROLL_HYST_WEEKS = 2;
//    // How long the GoTo fling animation should last
//    protected static final int GOTO_SCROLL_DURATION = 250;
//    // How long to wait after receiving an onScrollStateChanged notification
//    // before acting on it
//    protected static final int SCROLL_CHANGE_DELAY = 40;
//    public static int LIST_TOP_OFFSET = -1; // so that the top line will be under the separator

    // TODO: Delete, not used in original code?
    // The number of days to display in each week
    public static final int DAYS_PER_WEEK = 7;

    static int MONTH_NAVIGATION_BAR_SIZE;

    // TODO: Delete, not used in original code?
    // You can override these numbers to get a different appearance
    protected int mNumWeeks = 6;
    protected boolean mShowWeekNumber = false;
    protected int mDaysPerWeek = 7;

    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());

    // TODO: Delete, related to LIstView.
    // These affect the scroll speed and feel
//    protected float mFriction = 1.0f;

    protected Handler mHandler;

    // highlighted time
    protected CalendarDay mSelectedDay = new CalendarDay();
    protected PagingMonthAdapter mAdapter;

    private ViewAnimator mMonthAnimator;
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

    // TODO: Delete, not used in original code?
//    // When the week starts; numbered like Time.<WEEKDAY> (e.g. SUNDAY=0).
//    protected int mFirstDayOfWeek;
//    // The last name announced by accessibility
//    protected CharSequence mPrevMonthName;
    // which month should be displayed/highlighted [0-11]
    protected int mCurrentMonthDisplayed;
    private int mCurrentView = DAY_PICKER_INDEX;
    // The year associated with the current MonthView displayed
    private int mCurrentYearDisplayed;

    // TODO: Delete, related to LIstView.
//    // used for tracking during a scroll
//    protected long mPreviousScrollPosition;
//    // used for tracking what state listview is in
//    protected int mPreviousScrollState = OnScrollListener.SCROLL_STATE_IDLE;
//    // used for tracking what state listview is in
//    protected int mCurrentScrollState = OnScrollListener.SCROLL_STATE_IDLE;

    private DatePickerController mController;

    // TODO: Delete, related to LIstView.
//    private boolean mPerformingScroll;

    private boolean mThemeDark;

    public PagingDayPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PagingDayPickerView(Context context, DatePickerController controller) {
        this(context, controller, false);
    }

    public PagingDayPickerView(Context context, DatePickerController controller, boolean themeDark) {
        super(context);
        mThemeDark = themeDark;  // keep this before init()
        init(context);
        setController(controller);
    }

    public void setController(DatePickerController controller) {
        mController = controller;
        mController.registerOnDateChangedListener(this);
        refreshAdapter();
        onDateChanged();
        mMonthPickerView.setDatePickerController(mController);
        // keep this after onDateChanged() so that mSelectedDay is fully initialized
//        mMonthPickerView.setDisplayParams(mSelectedDay);  // not needed?
    }

    private void init(Context context) {
        mHandler = new Handler();
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
//        setDrawSelectorOnTop(false);  // TODO: Delete? Don't think there's a proper replacement.

        Resources res = getResources();
        MONTH_NAVIGATION_BAR_SIZE = res.getDimensionPixelOffset(R.dimen.month_navigation_bar_height)
                + res.getDimensionPixelOffset(R.dimen.month_view_top_padding);

        final View view = LayoutInflater.from(context).inflate(R.layout.day_picker_content, this, true);
        mMonthAnimator = (ViewAnimator) findViewById(R.id.month_animator);
        mMonthPickerView = (MonthPickerView) findViewById(R.id.month_picker);
        mMonthPickerView.setOnMonthClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.addOnPageChangeListener(this);
        mMonthYearTitleView = (TextView) view.findViewById(R.id.month_year_title);
        mTitleContainer = view.findViewById(R.id.month_year_title_container);
        mTitleContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int newIndex = mCurrentView == DAY_PICKER_INDEX ? MONTH_PICKER_INDEX : DAY_PICKER_INDEX;
                boolean arrowsVisible = newIndex == DAY_PICKER_INDEX;
                setCurrentView(newIndex);
                toggleArrowsVisibility(arrowsVisible, arrowsVisible);
                animateDropdown(newIndex);
                if (arrowsVisible) {
                    setTitle(mAdapter.getPageTitle(mViewPager.getCurrentItem()));
                } else {
                    // Fortunately, very few locales have a year pattern string different
                    // from "yyyy". Localization isn't too important here.
                    // TODO: Decide if you really want the year to be localized.
                    setTitle(String.valueOf(mCurrentYearDisplayed));
                }
            }
        });
        mPreviousButton = (ImageButton) view.findViewById(R.id.prev);
        mPreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousItem = mViewPager.getCurrentItem() - 1;
                if (previousItem >= 0) {
                    mViewPager.setCurrentItem(previousItem, true);
                }
            }
        });
        mNextButton = (ImageButton) view.findViewById(R.id.next);
        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextItem = mViewPager.getCurrentItem() + 1;
                if (nextItem < mAdapter.getCount()) {
                    mViewPager.setCurrentItem(nextItem, true);
                }
            }
        });

        mArrowDownDrawable = AnimatedVectorDrawableCompat.create(context, R.drawable.animated_arrow_drop_down);
        mArrowUpDrawable   = AnimatedVectorDrawableCompat.create(context, R.drawable.animated_arrow_drop_up);
        setArrowDrawableOnTitle(mArrowDownDrawable);

        // Theme-specific configurations.
        if (mThemeDark) {
            int selectableItemBg = getColor(context, R.color.selectable_item_background_dark);
            Utils.setColorControlHighlight(mPreviousButton, selectableItemBg);
            Utils.setColorControlHighlight(mNextButton, selectableItemBg);
            Utils.setColorControlHighlight(mTitleContainer, selectableItemBg);
            int cursor = getColor(context, R.color.text_color_secondary_dark);
            Utils.applyTint(mPreviousButton, cursor);
            Utils.applyTint(mNextButton, cursor);
        }

        // Set up colors.
        int monthYearTitleColor = getColor(context, mThemeDark?
                R.color.text_color_primary_dark : R.color.text_color_primary_light);
        int dropdownArrowColor = getColor(context, mThemeDark?
                R.color.icon_color_active_dark : R.color.icon_color_active_light);

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

    public void onChange() {
        refreshAdapter();
        refreshMonthPicker();
    }

    /**
     * Creates a new adapter if necessary and sets up its parameters. Override
     * this method to provide a custom adapter.
     */
    protected void refreshAdapter() {
        if (mAdapter == null) {
            mAdapter = createMonthAdapter(getContext(), mController, mThemeDark);
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
        return new PagingMonthAdapter(context, controller, themeDark);
    }

//    /*
//     * Sets all the required fields for the list view. Override this method to
//     * set a different list view behavior.
//     */
//    protected void setUpListView() {
//        // Transparent background on scroll
//        setCacheColorHint(0);
//        // No dividers
//        setDivider(null);
//        // Items are clickable
//        setItemsCanFocus(true);
//        // The thumb gets in the way, so disable it
//        setFastScrollEnabled(false);
//        setVerticalScrollBarEnabled(false);
//        setOnScrollListener(this);
//        setFadingEdgeLength(0);
//        // Make the scrolling behavior nicer
//        setFriction(ViewConfiguration.getScrollFriction() * mFriction);
//    }

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

        // Set the selected day
        if (setSelected) {
            mSelectedDay.set(day);
        }

        mTempDay.set(day);
        final int position = (day.year - mController.getMinYear())
                * MONTHS_IN_YEAR + day.month;

        // =================================================================
        // TODO: This whole section doesn't seem necessary at all.
        View child;
        int i = 0;
        int top = 0;
        // Find a child that's completely in the view
        do {
            child = getChildAt(i++);
            if (child == null) {
                break;
            }
            top = child.getTop();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "child at " + (i - 1) + " has top " + top);
            }
        } while (top < 0);

        // Compute the first and last position visible
        int selectedPosition;
        if (child != null) {
            // TODO: Verify this is an appropriate replacement.
            selectedPosition = /*getPositionForView(child);*/ mAdapter.getItemPosition(child);
        } else {
            selectedPosition = 0;
        }
        // =================================================================

        if (setSelected) {
            mAdapter.setSelectedDay(mSelectedDay);
//            mMonthPickerView.setDisplayParams(mSelectedDay);
        }

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "GoTo position " + position);
        }
        // Check if the selected day is now outside of our visible range
        // and if so scroll to the month that contains it
        if (position != selectedPosition || forceScroll) {
            setMonthDisplayed(mTempDay);
//            mPreviousScrollState = OnScrollListener.SCROLL_STATE_FLING;
            if (animate) {
//                smoothScrollToPositionFromTop(
//                        position, LIST_TOP_OFFSET, GOTO_SCROLL_DURATION);
                // TODO: Verify this is an appropriate replacement.
                mViewPager.setCurrentItem(position, true);
                return true;
            } else {
                postSetSelection(position);
            }
        } else if (setSelected) {
            setMonthDisplayed(mSelectedDay);
        }
        return false;
    }

    public void postSetSelection(final int position) {
        clearFocus();
        post(new Runnable() {

            @Override
            public void run() {
                mViewPager.setCurrentItem(position, false);
            }
        });
//        onScrollStateChanged(this, OnScrollListener.SCROLL_STATE_IDLE);
    }

//    /**
//     * Updates the title and selected month if the view has moved to a new
//     * month.
//     */
//    @Override
//    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        MonthView child = (MonthView) view.getChildAt(0);
//        if (child == null) {
//            return;
//        }
//
//        // Figure out where we are
//        long currScroll = view.getFirstVisiblePosition() * child.getHeight() - child.getBottom();
//        mPreviousScrollPosition = currScroll;
//        mPreviousScrollState = mCurrentScrollState;
//    }

    /**
     * Sets the month displayed at the top of this view based on time. Override
     * to add custom events when the title is changed.
     */
    protected void setMonthDisplayed(CalendarDay date) {
        mCurrentMonthDisplayed = date.month;
//        invalidateViews();
//        invalidate();  // not necessary?
    }

//    @Override
//    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        // use a post to prevent re-entering onScrollStateChanged before it
//        // exits
//        mScrollStateChangedRunnable.doScrollStateChange(view, scrollState);
//    }

//    protected ScrollStateRunnable mScrollStateChangedRunnable = new ScrollStateRunnable();

//    protected class ScrollStateRunnable implements Runnable {
//        private int mNewState;
//
//        /**
//         * Sets up the runnable with a short delay in case the scroll state
//         * immediately changes again.
//         *
//         * @param view The list view that changed state
//         * @param scrollState The new state it changed to
//         */
//        public void doScrollStateChange(AbsListView view, int scrollState) {
//            mHandler.removeCallbacks(this);
//            mNewState = scrollState;
//            mHandler.postDelayed(this, SCROLL_CHANGE_DELAY);
//        }
//
//        @Override
//        public void run() {
//            mCurrentScrollState = mNewState;
//            if (Log.isLoggable(TAG, Log.DEBUG)) {
//                Log.d(TAG,
//                        "new scroll state: " + mNewState + " old state: " + mPreviousScrollState);
//            }
//            // Fix the position after a scroll or a fling ends
//            if (mNewState == OnScrollListener.SCROLL_STATE_IDLE
//                    && mPreviousScrollState != OnScrollListener.SCROLL_STATE_IDLE
//                    && mPreviousScrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                mPreviousScrollState = mNewState;
//                int i = 0;
//                View child = getChildAt(i);
//                while (child != null && child.getBottom() <= 0) {
//                    child = getChildAt(++i);
//                }
//                if (child == null) {
//                    // The view is no longer visible, just return
//                    return;
//                }
//                int firstPosition = getFirstVisiblePosition();
//                int lastPosition = getLastVisiblePosition();
//                boolean scroll = firstPosition != 0 && lastPosition != getCount() - 1;
//                final int top = child.getTop();
//                final int bottom = child.getBottom();
//                final int midpoint = getHeight() / 2;
//                if (scroll && top < LIST_TOP_OFFSET) {
//                    if (bottom > midpoint) {
//                        smoothScrollBy(top, GOTO_SCROLL_DURATION);
//                    } else {
//                        smoothScrollBy(bottom, GOTO_SCROLL_DURATION);
//                    }
//                }
//            } else {
//                mPreviousScrollState = mNewState;
//            }
//        }
//    }
//
//    /**
//     * Gets the position of the view that is most prominently displayed within the list view.
//     */
//    public int getMostVisiblePosition() {
//        final int firstPosition = getFirstVisiblePosition();
//        final int height = getHeight();
//
//        int maxDisplayedHeight = 0;
//        int mostVisibleIndex = 0;
//        int i=0;
//        int bottom = 0;
//        while (bottom < height) {
//            View child = getChildAt(i);
//            if (child == null) {
//                break;
//            }
//            bottom = child.getBottom();
//            int displayedHeight = Math.min(bottom, height) - Math.max(0, child.getTop());
//            if (displayedHeight > maxDisplayedHeight) {
//                mostVisibleIndex = i;
//                maxDisplayedHeight = displayedHeight;
//            }
//            i++;
//        }
//        return firstPosition + mostVisibleIndex;
//    }

    @Override
    public void onDateChanged() {
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

    // TODO: What do we replace this with?
//    protected void layoutChildren() {
//        final CalendarDay focusedDay = findAccessibilityFocus();
//        super.layoutChildren();
//        if (mPerformingScroll) {
//            mPerformingScroll = false;
//        } else {
//            restoreAccessibilityFocus(focusedDay);
//        }
//    }

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
        int firstVisiblePosition = /*getFirstVisiblePosition()*/-123; // TODO: What do we replace this with?
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
//        mPerformingScroll = true;
        return true;
    }

    int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setTitle(mAdapter.getPageTitle(position));
        toggleArrowsVisibility(position > 0, position + 1 < mAdapter.getCount());

        final int month = position % MONTHS_IN_YEAR;
        final int year = position / MONTHS_IN_YEAR + mController.getMinYear();
        if (mCurrentYearDisplayed != year) {
            mCurrentYearDisplayed = year;
        }
        if (mCurrentMonthDisplayed != month) {
            mCurrentMonthDisplayed = month;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setTitle(CharSequence title) {
        mMonthYearTitleView.setText(title);
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

    /**
     * @param viewIndex The index being switched to
     */
    private void animateDropdown(final int viewIndex) {
        switch (viewIndex) {
            case DAY_PICKER_INDEX:
                setArrowDrawableOnTitle(mArrowUpDrawable);
                mArrowUpDrawable.start();
                break;
            case MONTH_PICKER_INDEX:
                setArrowDrawableOnTitle(mArrowDownDrawable);
                mArrowDownDrawable.start();
                break;
        }
    }

    private void setCurrentView(final int viewIndex) {
//        long millis = mCalendar.getTimeInMillis();

        switch (viewIndex) {
            case DAY_PICKER_INDEX:
//                mDayPickerView.onDateChanged();
                if (mCurrentView != viewIndex) {
//                    updateHeaderSelectedView(MONTH_AND_DAY_VIEW);
                    mMonthAnimator.setDisplayedChild(DAY_PICKER_INDEX);
                    mCurrentView = viewIndex;
                }

//                int flags = DateUtils.FORMAT_SHOW_DATE;
//                String dayString = DateUtils.formatDateTime(getActivity(), millis, flags);
//                mAnimator.setContentDescription(mDayPickerDescription + ": " + dayString);
//                Utils.tryAccessibilityAnnounce(mAnimator, mSelectDay);
                break;
            case MONTH_PICKER_INDEX:
//                mYearPickerView.onDateChanged();
                prepareMonthPickerForDisplay(mCurrentYearDisplayed);
                if (mCurrentView != viewIndex) {
//                    updateHeaderSelectedView(YEAR_VIEW);
                    mMonthAnimator.setDisplayedChild(MONTH_PICKER_INDEX);
                    mCurrentView = viewIndex;
                }

//                CharSequence yearString = YEAR_FORMAT.format(millis);
//                mAnimator.setContentDescription(mYearPickerDescription + ": " + yearString);
//                Utils.tryAccessibilityAnnounce(mAnimator, mSelectYear);
                break;
        }
    }

    private void prepareMonthPickerForDisplay(int currentYear) {
        mMonthPickerView.setDisplayParams(mSelectedDay, currentYear);
    }

    @Override
    public void onMonthClick(MonthPickerView view, int month, int year) {
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
        setCurrentView(DAY_PICKER_INDEX);
    }
}
