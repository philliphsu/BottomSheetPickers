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

package com.philliphsu.bottomsheettimepickers.timepickers;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.philliphsu.clock2.R;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Phillip Hsu on 7/12/2016.
 *
 * Successor to the Numpad class that was based on TableLayout.
 *
 * TODO: Is NumpadTimePicker the only subclass? If so, why do we need this
 * superclass? If we move the contents of this class to NumpadTimePicker,
 * the implementation of setTheme() would make more sense.
 */
public abstract class GridLayoutNumpad extends GridLayout {
    // TODO: change to private?
    protected static final int UNMODIFIED = -1;
    private static final int COLUMNS = 3;

    private int[] mInput;
    private int mCount = 0;
    private OnInputChangeListener mOnInputChangeListener;

    private ColorStateList mTextColors;
    int mAccentColor;

    @Bind({ R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four,
            R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine })
    TextView[] mButtons;

    /**
     * Informs clients how to output the digits inputted into this numpad.
     */
    public interface OnInputChangeListener {
        /**
         * @param newStr the new value of the input formatted as a
         *               String after a digit insertion
         */
        void onDigitInserted(String newStr);
        /**
         * @param newStr the new value of the input formatted as a
         *               String after a digit deletion
         */
        void onDigitDeleted(String newStr);
        void onDigitsCleared();
    }

    public GridLayoutNumpad(Context context) {
        this(context, null);
    }

    public GridLayoutNumpad(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void setTheme(Context context, boolean themeDark) {
        // Since the Dialog class already set the background color of its entire view tree,
        // our background is already colored. Why did we set it in the Dialog class? Because
        // we use margins around the numpad, and if we had instead set the background on
        // this numpad here, the margins will not be colored. Why not use padding instead
        // of margins? It turns out we tried that--replacing each margin attribute
        // with the padding counterpart--but we lost the pre-21 FAB inherent bottom margin.

        // The buttons are actually of type Button, but we kept references
        // to them as TextViews... which is fine since TextView is the superclass
        // of Button.
        mTextColors = ContextCompat.getColorStateList(context, themeDark?
                R.color.numeric_keypad_button_text_dark : R.color.numeric_keypad_button_text);

        // AFAIK, the only way to get the user's accent color is programmatically,
        // because it is uniquely defined in their app's theme. It is not possible
        // for us to reference that via XML (i.e. with ?colorAccent or similar),
        // which happens at compile time.
        // TOneverDO: Use any other Context to retrieve the accent color. We must use
        // the Context param passed to us, because we know this context to be
        // NumpadTimePickerDialog.getContext(), which is equivalent to
        // NumpadTimePickerDialog.getActivity(). It is from that Activity where we
        // get its theme's colorAccent.
        mAccentColor = Utils.getThemeAccentColor(context);
        for (TextView b : mButtons) {
            setTextColor(b);
            Utils.setColorControlHighlight(b, mAccentColor);
        }
    }

    void setTextColor(TextView view) {
        view.setTextColor(mTextColors);
    }

    /**
     * @return the number of digits we can input
     */
    public abstract int capacity();

    @LayoutRes
    protected abstract int contentLayout();

    public final void setOnInputChangeListener(OnInputChangeListener onInputChangeListener) {
        mOnInputChangeListener = onInputChangeListener;
    }

    /**
     * Provided only for subclasses so they can retrieve the registered listener
     * and fire any custom OnInputChange events they may have defined.
     */
    protected final OnInputChangeListener getOnInputChangeListener() {
        return mOnInputChangeListener;
    }

    @CallSuper
    protected void enable(int lowerLimitInclusive, int upperLimitExclusive) {
        if (lowerLimitInclusive < 0 || upperLimitExclusive > mButtons.length)
            throw new IndexOutOfBoundsException("Upper limit out of range");

        for (int i = 0; i < mButtons.length; i++)
            mButtons[i].setEnabled(i >= lowerLimitInclusive && i < upperLimitExclusive);
    }

    protected final int valueAt(int index) {
        return mInput[index];
    }

    /**
     * @return a defensive copy of the internal array of inputted digits
     */
    protected final int[] getDigits() {
        int[] digits = new int[mInput.length];
        System.arraycopy(mInput, 0, digits, 0, mInput.length);
        return digits;
    }

    /**
     * @return the number of digits inputted
     */
    public final int count() {
        return mCount;
    }

    /**
     * @return the integer represented by the inputted digits
     */
    protected final int getInput() {
        return Integer.parseInt(getInputString());
    }

    private String getInputString() {
        String currentInput = "";
        for (int i : mInput)
            if (i != UNMODIFIED)
                currentInput += i;
        return currentInput;
    }

    public void delete() {
        /*
        if (mCount - 1 >= 0) {
            mInput[--mCount] = UNMODIFIED;
        }
        onDigitDeleted(getInputString());
        */
        delete(mCount);
    }

    // TODO: Why do we need this?
    @Deprecated
    public void delete(int at) {
        if (at - 1 >= 0) {
            mInput[at - 1] = UNMODIFIED;
            mCount--;
            onDigitDeleted(getInputString());
        }
    }

    public boolean clear() {
        Arrays.fill(mInput, UNMODIFIED);
        mCount = 0;
        onDigitsCleared();
        return true;
    }

    /**
     * Forwards the provided String to the assigned
     * {@link OnInputChangeListener OnInputChangeListener}
     * after a digit insertion. By default, the String
     * forwarded is just the String value of the inserted digit.
     * @see #onClick(TextView)
     * @param newDigit the formatted String that should be displayed
     */
    @CallSuper
    protected void onDigitInserted(String newDigit) {
        if (mOnInputChangeListener != null) {
            mOnInputChangeListener.onDigitInserted(newDigit);
        }
    }

    /**
     * Forwards the provided String to the assigned
     * {@link OnInputChangeListener OnInputChangeListener}
     * after a digit deletion. By default, the String
     * forwarded is {@link #getInputString()}.
     * @param newStr the formatted String that should be displayed
     */
    @CallSuper
    protected void onDigitDeleted(String newStr) {
        if (mOnInputChangeListener != null) {
            mOnInputChangeListener.onDigitDeleted(newStr);
        }
    }

    /**
     * Forwards a {@code onDigitsCleared()} event to the assigned
     * {@link OnInputChangeListener OnInputChangeListener}.
     */
    @CallSuper
    protected void onDigitsCleared() {
        if (mOnInputChangeListener != null) {
            mOnInputChangeListener.onDigitsCleared();
        }
    }

    /**
     * Inserts as many of the digits in the given sequence
     * into the input as possible. At the end, if any digits
     * were inserted, this calls {@link #onDigitInserted(String)}
     * with the String value of those digits.
     */
    protected final void insertDigits(int... digits) {
        if (digits == null)
            return;
        String newDigits = "";
        for (int d : digits) {
            if (mCount == mInput.length)
                break;
            if (d == UNMODIFIED)
                continue;
            mInput[mCount++] = d;
            newDigits += d;
        }
        if (!newDigits.isEmpty()) {
            // By only calling this once after making
            // the insertions, we skip all of the
            // intermediate callbacks.
            onDigitInserted(newDigits);
        }
    }

    @OnClick({ R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four,
            R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine })
    final void onClick(TextView view) {
        if (mCount < mInput.length) {
            String textNum = view.getText().toString();
            insertDigits(Integer.parseInt(textNum));
        }
    }

    private void init() {
        setAlignmentMode(ALIGN_BOUNDS);
        setColumnCount(COLUMNS);
        View.inflate(getContext(), contentLayout(), this);
        ButterKnife.bind(this);
        // If capacity() < 0, we let the system throw the exception.
        mInput = new int[capacity()];
        Arrays.fill(mInput, UNMODIFIED);
    }
}
