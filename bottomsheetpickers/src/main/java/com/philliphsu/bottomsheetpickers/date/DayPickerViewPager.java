/*
 * Copyright (C) 2015 The Android Open Source Project
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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.philliphsu.bottomsheetpickers.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * This displays a list of months in a calendar format with selectable days.
 */
public class DayPickerViewPager extends ViewPager {
    private static final String TAG = DayPickerViewPager.class.getSimpleName();

    private final ArrayList<View> mMatchParentChildren = new ArrayList<>(1);

    private Method  mPopulateMethod;
    private boolean mAlreadyTriedAccessingMethod;

    public DayPickerViewPager(Context context) {
        this(context, null);
    }

    public DayPickerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // populate();
        // Use reflection
        callPopulate();

        // Everything below is mostly copied from FrameLayout.
        int count = getChildCount();

        final boolean measureMatchParentChildren =
                MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY ||
                        MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
                childState = combineMeasuredStates(childState, child.getMeasuredState());
                if (measureMatchParentChildren) {
                    if (lp.width == LayoutParams.MATCH_PARENT ||
                            lp.height == LayoutParams.MATCH_PARENT) {
                        mMatchParentChildren.add(child);
                    }
                }
            }
        }

        // Account for padding too
        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        if (Utils.checkApiLevel(Build.VERSION_CODES.M)) {
            // Check against our foreground's minimum height and width
            final Drawable drawable = getForeground();
            if (drawable != null) {
                maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
                maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
            }
        }

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));

        count = mMatchParentChildren.size();
        if (count > 1) {
            for (int i = 0; i < count; i++) {
                final View child = mMatchParentChildren.get(i);

                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final int childWidthMeasureSpec;
                final int childHeightMeasureSpec;

                if (lp.width == LayoutParams.MATCH_PARENT) {
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                            getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                            MeasureSpec.EXACTLY);
                } else {
                    childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                            getPaddingLeft() + getPaddingRight(),
                            lp.width);
                }

                if (lp.height == LayoutParams.MATCH_PARENT) {
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                            getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
                            MeasureSpec.EXACTLY);
                } else {
                    childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                            getPaddingTop() + getPaddingBottom(),
                            lp.height);
                }

                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }

        mMatchParentChildren.clear();
    }

    /* ****************** The parent method defined in View is hidden. ******************** */
//    @Override
//    protected View findViewByPredicateTraversal(Predicate<View> predicate, View childToSkip) {
//        if (predicate.apply(this)) {
//            return this;
//        }
//
//        // Always try the selected view first.
//        final DayPickerPagerAdapter adapter = (DayPickerPagerAdapter) getAdapter();
//        final SimpleMonthView current = adapter.getView(getCurrent());
//        if (current != childToSkip && current != null) {
//            final View v = current.findViewByPredicate(predicate);
//            if (v != null) {
//                return v;
//            }
//        }
//
//        final int len = getChildCount();
//        for (int i = 0; i < len; i++) {
//            final View child = getChildAt(i);
//
//            if (child != childToSkip && child != current) {
//                final View v = child.findViewByPredicate(predicate);
//
//                if (v != null) {
//                    return v;
//                }
//            }
//        }
//
//        return null;
//    }

    private void initializePopulateMethod() {
        try {
            mPopulateMethod = ViewPager.class.getDeclaredMethod("populate", (Class[]) null);
            mPopulateMethod.setAccessible(true);
        } catch (NoSuchMethodException nsme) {
            nsme.printStackTrace();
        }

        mAlreadyTriedAccessingMethod = true;
    }

    private void callPopulate() {
        if (!mAlreadyTriedAccessingMethod) {
            initializePopulateMethod();
        }

        if (mPopulateMethod != null) {
            // Multi-catch block cannot be used before API 19
            //noinspection TryWithIdenticalCatches
            try {
                mPopulateMethod.invoke(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Could not call `ViewPager.populate()`");
        }
    }
}