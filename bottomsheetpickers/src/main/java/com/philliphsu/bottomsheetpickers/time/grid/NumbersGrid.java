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

package com.philliphsu.bottomsheetpickers.time.grid;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.Utils;
import com.philliphsu.bottomsheetpickers.time.TimePickerPadLayout;

public abstract class NumbersGrid extends TimePickerPadLayout implements View.OnClickListener {
    private static final String TAG = "NumbersGrid";

    // Package visible so our concrete subclasses (in the same package) can access this.
    OnNumberSelectedListener mSelectionListener;
    View mLastSelectedView;

    int mSelectedTextColor;
    // TODO: The half-day buttons in the dialog's layout also need to use this color.
    // Consider moving this to either the Dialog class, or move the buttons and the FAB
    // to the GridSelectorLayout class and then move these to GridSelectorLayout.
    int mDefaultTextColor;

    private boolean mIsInitialized;
    private int mSelection; // The number selected from this grid

    public interface OnNumberSelectedListener {
        void onNumberSelected(int number);
    }

    public NumbersGrid(Context context) {
        this(context, null);
    }

    public NumbersGrid(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumbersGrid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mIsInitialized = false;
        mDefaultTextColor = ContextCompat.getColor(context, R.color.bsp_text_color_primary_light);
        // The reason we can use the Context passed here and get the correct accent color
        // is that this NumbersGrid is programmatically created by the GridSelectorLayout in
        // its initialize(), and the Context passed in there is from
        // GridTimePickerDialog.getActivity().
        mSelectedTextColor = Utils.getThemeAccentColor(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        registerClickListeners();
        final View defaultSelectedView = getChildAt(indexOfDefaultValue());
        mSelection = valueOf(defaultSelectedView);
        setIndicator(defaultSelectedView);
    }

    public void initialize(OnNumberSelectedListener listener) {
        if (mIsInitialized) {
            Log.e(TAG, "This NumbersGrid may only be initialized once.");
            return;
        }
        mSelectionListener = listener;
        mIsInitialized = true;
    }

    public int getSelection() {
        return mSelection;
    }

    public void setSelection(int value) {
        mSelection = value;
    }

    /**
     * The default implementation assumes the clicked view is of type TextView,
     * casts the view accordingly, and parses the number from the text it contains.
     * @param v the View that was clicked
     */
    @Override
    public void onClick(View v) {
        setIndicator(v);
        mSelection = valueOf(v);
        mSelectionListener.onNumberSelected(mSelection);
    }

    /**
     * Returns whether the specified View from our hierarchy can have an
     * OnClickListener registered on it. The default implementation
     * checks if this view is of type TextView. Subclasses can override
     * this to fit their own criteria of what types of Views in their
     * hierarchy can have a click listener registered on.
     *
     * @param view a child view from our hierarchy
     */
    protected boolean canRegisterClickListener(View view) {
        return view instanceof TextView;
    }

    /**
     * Sets a selection indicator on the clicked number button. The indicator
     * is the accent color applied to the button's text.
     *
     * @param view the clicked number button
     */
    protected void setIndicator(View view) {
        clearIndicator(); // Does nothing if there was no indicator last selected
        TextView tv = (TextView) view;
        tv.setTextColor(mSelectedTextColor);
        tv.setTypeface(Utils.SANS_SERIF_THIN_BOLD);
        mLastSelectedView = view;
    }

    /**
     * Clear the selection indicator on the last selected view. Clearing the indicator
     * reverts the text color back to its default.
     */
    protected void clearIndicator() {
        if (mLastSelectedView != null) {
            TextView tv = (TextView) mLastSelectedView;
            tv.setTextColor(mDefaultTextColor);
            tv.setTypeface(Utils.isJellybeanOrLater() ? Utils.SANS_SERIF_LIGHT : Typeface.DEFAULT);
            mLastSelectedView = null;
        }
    }

    /**
     * @return the index for the number button that should have the indicator set on by default.
     * The base implementation returns 0, for the first child.
     */
    protected int indexOfDefaultValue() {
        return 0;
    }

    /**
     * @return the number held by the button parsed into an integer. The base implementation
     * assumes the view is of type TextView.
     */
    protected int valueOf(View button) {
        return Integer.parseInt(((TextView) button).getText().toString());
    }

    /**
     * The default implementation sets the appropriate text color on all of the number buttons
     * as determined by {@link #canRegisterClickListener(View)}.
     */
    void setTheme(Context context, boolean themeDark) {
        mDefaultTextColor = ContextCompat.getColor(context, themeDark?
                R.color.bsp_text_color_primary_dark : R.color.bsp_text_color_primary_light);
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            // TODO: We can move this to the ctor, because this isn't dependent on the theme.
            // The only issue is we would have to write another for loop iterating through all
            // the buttons... but that is just prematurely worrying about optimizations..
            Utils.setColorControlHighlight(v, mSelectedTextColor/*colorAccent*/);
            // Filter out views that aren't number buttons
            if (canRegisterClickListener(v)) {
                final TextView tv = (TextView) v;
                // Filter out the current selection
                if (mSelection != valueOf(tv)) {
                    tv.setTextColor(mDefaultTextColor);
                }
            }
        }
    }

    void setAccentColor(@ColorInt int color) {
        mSelectedTextColor = color;
    }

    /**
     * Iterates through our hierarchy and sets the subclass's implementation of OnClickListener
     * on each number button encountered. By default, the number buttons are assumed to be of
     * type TextView.
     */
    private void registerClickListeners() {
        int i = 0;
        View v;
        while (i < getChildCount() && canRegisterClickListener(v = getChildAt(i))) {
            v.setOnClickListener(this);
            i++;
        }
    }
}
