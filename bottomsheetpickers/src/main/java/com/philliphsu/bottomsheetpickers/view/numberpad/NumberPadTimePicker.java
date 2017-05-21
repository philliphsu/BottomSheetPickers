package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// TODO: Declare an attribute with format="reference" to allow a style resource to be specified.
class NumberPadTimePicker extends LinearLayout implements INumberPadTimePicker.View {
    /**
     * Color attributes defined in our {@code Context}'s theme.
     *
     * Used only in {@link #LAYOUT_BOTTOM_SHEET} to set the default colors for the FAB in code,
     * rather than in XML because themed colors can only be resolved in code below API 23.
     */
    private static final int[] ATTRS_FAB_COLORS = { R.attr.colorButtonNormal, R.attr.colorAccent };

    /** Option to place the backspace button in the header. */
    private static final int LOCATION_HEADER = 0;
    /** Option to place the backspace button in the footer. */
    private static final int LOCATION_FOOTER = 1;

    @IntDef({LOCATION_HEADER, LOCATION_FOOTER})
    @Retention(RetentionPolicy.SOURCE)
    private @interface BackspaceLocation {}

    /** Option to layout this view for use in an alert dialog. */
    static final int LAYOUT_ALERT = 1;
    /** Option to layout this view for use in a bottom sheet dialog. */
    static final int LAYOUT_BOTTOM_SHEET = 2;

    @IntDef({LAYOUT_ALERT, LAYOUT_BOTTOM_SHEET})
    @Retention(RetentionPolicy.SOURCE)
    @interface NumberPadTimePickerLayout {}

    /** Option to always show the FAB. */
    static final int SHOW_FAB_ALWAYS = 0;
    /** Option to only show the FAB when the inputted sequence makes a valid time. */
    static final int SHOW_FAB_VALID_TIME = 1;

    @IntDef({SHOW_FAB_ALWAYS, SHOW_FAB_VALID_TIME})
    @Retention(RetentionPolicy.SOURCE)
    @interface ShowFabPolicy {}

    private NumberPadView mNumberPad;
    private LinearLayout mInputTimeContainer;
    private TextView mTimeDisplay;
    private TextView mAmPmDisplay;
    private ImageButton mBackspace;
    private @Nullable View mOkButton;

    private @NumberPadTimePickerLayout int mLayout;
    private @ShowFabPolicy int mShowFabPolicy;
    private boolean mAnimateFabIn;

    public NumberPadTimePicker(Context context) {
        this(context, null);
    }

    public NumberPadTimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0 /*TODO: Pass our attribute here. This contains a reference to a style resource.*/);
    }

    public NumberPadTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public NumberPadTimePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    // TODO: Apply the style resource, either the one contained in defStyleAttr or defStyleRes itself.
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray timePickerAttrs = context.obtainStyledAttributes(attrs,
                R.styleable.BSP_NumberPadTimePicker, defStyleAttr, defStyleRes);

        setOrientation(VERTICAL);
        mLayout = retrieveLayout(timePickerAttrs);
        final @LayoutRes int layoutRes = mLayout == LAYOUT_BOTTOM_SHEET
                ? R.layout.bsp_bottomsheet_numberpad_time_picker
                : R.layout.bsp_numberpad_time_picker;
        inflate(context, layoutRes, this);

        mNumberPad = (NumberPadView) findViewById(R.id.bsp_numberpad_time_picker_view);
        mInputTimeContainer = (LinearLayout) findViewById(R.id.bsp_input_time_container);
        mTimeDisplay = (TextView) findViewById(R.id.bsp_input_time);
        mAmPmDisplay = (TextView) findViewById(R.id.bsp_input_ampm);
        mBackspace = (ImageButton) findViewById(R.id.bsp_backspace);
        mOkButton = findViewById(R.id.bsp_ok_button);
        final ViewGroup headerView = (ViewGroup) findViewById(R.id.bsp_header);

        if (mLayout == LAYOUT_BOTTOM_SHEET) {
            final FloatingActionButton fab = (FloatingActionButton) mOkButton;
            final ColorStateList fabBackgroundColor = retrieveFabBackgroundColor(
                    timePickerAttrs, context);
            final int fabRippleColor = timePickerAttrs.getColor(
                    R.styleable.BSP_NumberPadTimePicker_bsp_fabRippleColor, 0);
            // If we could not create a default ColorStateList, then just leave the current
            // color as is.
            if (fabBackgroundColor != null) {
                // If we don't make this cast, this would call the base method in View,
                // which requires API 21.
                fab.setBackgroundTintList(fabBackgroundColor);
            }
            if (fabRippleColor != 0) {
                fab.setRippleColor(fabRippleColor);
            }

            mAnimateFabIn = timePickerAttrs.getBoolean(
                    R.styleable.BSP_NumberPadTimePicker_bsp_animateFabIn, false);
            mShowFabPolicy = retrieveShowFab(timePickerAttrs);
            // For the FAB to actually animate in, it cannot be visible initially.
            mOkButton.setVisibility(mAnimateFabIn || mShowFabPolicy == SHOW_FAB_VALID_TIME
                    ? INVISIBLE : VISIBLE);

            final int backspaceLocation = retrieveBackspaceLocation(timePickerAttrs);
            // We can't set the backspace location immediately because the views have not finished
            // drawing at this point. As such, setting {@code NumberPadView#getRowCount() - 1}
            // as the row index at which the backspace button should be added will not give us the
            // expected result. Note that {@code NumberPadView#getColumnCount() - 1} does return the
            // correct column index; this is because we explicitly set a column count.
            //
            // Instead of hardcoding the row index, which will not scale if we ever add or remove
            // rows to the number pad, we wait until everything is completely drawn to do our
            // manipulation. Note that drawing is not completed even by the time of onFinishInflate().
            mNumberPad.post(new Runnable() {
                @Override
                public void run() {
                    switch (backspaceLocation) {
                        case LOCATION_HEADER:
                            break;
                        case LOCATION_FOOTER:
                            headerView.removeView(mBackspace);
                            final GridLayout.LayoutParams lp = new GridLayout.LayoutParams(
                                    // The row of the cell in which the backspace key should go.
                                    // This specifies the row index, which spans one increment,
                                    // and indicates the cell should be filled along the row
                                    // (horizontal) axis.
                                    GridLayout.spec(mNumberPad.getRowCount() - 1, GridLayout.FILL),
                                    // The column of the cell in which the backspace key should go.
                                    // This specifies the column index, which spans one increment,
                                    // and indicates the cell should be filled along the column
                                    // (vertical) axis.
                                    GridLayout.spec(mNumberPad.getColumnCount() - 1, GridLayout.FILL));
                            mNumberPad.addView(mBackspace, lp);
                            break;
                    }
                }
            });
        }

        final int inputTimeTextColor = timePickerAttrs.getColor(
                R.styleable.BSP_NumberPadTimePicker_bsp_inputTimeTextColor, 0);
        final int inputAmPmTextColor = timePickerAttrs.getColor(
                R.styleable.BSP_NumberPadTimePicker_bsp_inputAmPmTextColor, 0);
        final ColorStateList backspaceTint = timePickerAttrs.getColorStateList(
                R.styleable.BSP_NumberPadTimePicker_bsp_backspaceTint);
        final ColorStateList numberKeysTextColor = timePickerAttrs.getColorStateList(
                R.styleable.BSP_NumberPadTimePicker_bsp_numberKeysTextColor);
        final ColorStateList altKeysTextColor = timePickerAttrs.getColorStateList(
                R.styleable.BSP_NumberPadTimePicker_bsp_altKeysTextColor);
        final Drawable headerBackground = timePickerAttrs.getDrawable(
                R.styleable.BSP_NumberPadTimePicker_bsp_headerBackground);
        final Drawable divider = timePickerAttrs.getDrawable(
                R.styleable.BSP_NumberPadTimePicker_bsp_divider);
        final Drawable numberPadBackground = timePickerAttrs.getDrawable(
                R.styleable.BSP_NumberPadTimePicker_bsp_numberPadBackground);
        timePickerAttrs.recycle();

        if (inputTimeTextColor != 0) {
            mTimeDisplay.setTextColor(inputTimeTextColor);
        }
        if (inputAmPmTextColor != 0) {
            mAmPmDisplay.setTextColor(inputAmPmTextColor);
        }
        if (backspaceTint != null) {
            DrawableCompat.setTintList(mBackspace.getDrawable(), backspaceTint);
        }
        if (numberKeysTextColor != null) {
            mNumberPad.setNumberKeysTextColor(numberKeysTextColor);
        }
        if (altKeysTextColor != null) {
            mNumberPad.setAltKeysTextColor(altKeysTextColor);
        }
        if (headerBackground != null) {
            setBackground(headerView, headerBackground);
        }
        if (divider != null) {
            setBackground(findViewById(R.id.bsp_divider), divider);
        }
        if (numberPadBackground != null) {
            setBackground(mNumberPad, numberPadBackground);
        }
    }

    @Override
    public void setNumberKeysEnabled(int start, int end) {
        mNumberPad.setNumberKeysEnabled(start, end);
    }

    @Override
    public void setBackspaceEnabled(boolean enabled) {
        mBackspace.setEnabled(enabled);
    }

    @Override
    public void updateTimeDisplay(CharSequence time) {
        mTimeDisplay.setText(time);
    }

    @Override
    public void updateAmPmDisplay(CharSequence ampm) {
        mAmPmDisplay.setText(ampm);
    }

    @Override
    public void setAmPmDisplayVisible(boolean visible) {
        mAmPmDisplay.setVisibility(visible ? VISIBLE : GONE);
    }

    @Override
    public void setAmPmDisplayIndex(int index) {
        if (index != 0 && index != 1) {
            throw new IllegalArgumentException("Index of AM/PM display must be 0 or 1. index == " + index);
        }
        if (index == 1) return;
        mInputTimeContainer.removeViewAt(1);
        mInputTimeContainer.addView(mAmPmDisplay, 0);
    }

    @Override
    public void setLeftAltKeyText(CharSequence text) {
        mNumberPad.setLeftAltKeyText(text);
    }

    @Override
    public void setRightAltKeyText(CharSequence text) {
        mNumberPad.setRightAltKeyText(text);
    }

    @Override
    public void setLeftAltKeyEnabled(boolean enabled) {
        mNumberPad.setLeftAltKeyEnabled(enabled);
    }

    @Override
    public void setRightAltKeyEnabled(boolean enabled) {
        mNumberPad.setRightAltKeyEnabled(enabled);
    }

    @Deprecated
    @Override
    public void setHeaderDisplayFocused(boolean focused) {
        // Do nothing.
    }

    @Nullable
    View getOkButton() {
        return mOkButton;
    }

    @NumberPadTimePickerLayout
    int getLayout() {
        return mLayout;
    }

    boolean isAnimateFabIn() {
        checkLayoutIsBottomSheet(mLayout);
        return mAnimateFabIn;
    }

    @ShowFabPolicy
    int getShowFabPolicy() {
        checkLayoutIsBottomSheet(mLayout);
        return mShowFabPolicy;
    }

    void setOnBackspaceClickListener(OnClickListener l) {
        mBackspace.setOnClickListener(l);
    }

    void setOnBackspaceLongClickListener(OnLongClickListener l) {
        mBackspace.setOnLongClickListener(l);
    }

    void setOnNumberKeyClickListener(OnClickListener l) {
        mNumberPad.setOnNumberKeyClickListener(l);
    }

    void setOnAltKeyClickListener(OnClickListener l) {
        mNumberPad.setOnAltKeyClickListener(l);
    }

    private static void checkLayoutIsBottomSheet(int layout) {
        if (layout != LAYOUT_BOTTOM_SHEET) {
            throw new UnsupportedOperationException("Layout must be LAYOUT_BOTTOM_SHEET to call this method");
        }
    }

    @Nullable
    private static ColorStateList retrieveFabBackgroundColor(
            TypedArray timePickerAttrs, Context context) {
        ColorStateList fabBackgroundColor = timePickerAttrs.getColorStateList(
                R.styleable.BSP_NumberPadTimePicker_bsp_fabBackgroundColor);
        if (fabBackgroundColor == null) {
            // Set default ColorStateList.
            // Themed color attributes in a ColorStateList defined in XML cannot be resolved
            // correctly below API 23, so we can only do this in code.

            // We must create a different TypedArray here rather than use the previous instance
            // because it was configured to only retrieve values for NumberPadTimePicker
            // attributes.
            final TypedArray themedColors = context.obtainStyledAttributes(ATTRS_FAB_COLORS);
            // The first argument in this set of calls is the index of the attribute in the
            // attributes array for which we would like to get the resolved value.
            // The second argument is the default value to return if a value for the attribute
            // could not be found.
            final int disabledColor = themedColors.getColor(0, 0);
            final int defaultColor = themedColors.getColor(1, 0);
            themedColors.recycle();
            if (disabledColor != 0 && defaultColor != 0) {
                int[][] states = {{-android.R.attr.state_enabled}, {}};
                int[] colors = {disabledColor, defaultColor};
                fabBackgroundColor = new ColorStateList(states, colors);
            }
        }
        return fabBackgroundColor;
    }

    @NumberPadTimePickerLayout
    private static int retrieveLayout(TypedArray timePickerAttrs) {
        final int layout = timePickerAttrs.getInt(
                R.styleable.BSP_NumberPadTimePicker_bsp_numberPadTimePickerLayout, LAYOUT_ALERT);
        switch (layout) {
            case LAYOUT_ALERT:
            case LAYOUT_BOTTOM_SHEET:
                return layout;
            default:
                return LAYOUT_ALERT;
        }
    }

    @ShowFabPolicy
    private static int retrieveShowFab(TypedArray timePickerAttrs) {
        final int policy = timePickerAttrs.getInt(
                R.styleable.BSP_NumberPadTimePicker_bsp_showFab, SHOW_FAB_ALWAYS);
        switch (policy) {
            case SHOW_FAB_ALWAYS:
            case SHOW_FAB_VALID_TIME:
                return policy;
            default:
                return SHOW_FAB_ALWAYS;
        }
    }

    @BackspaceLocation
    private static int retrieveBackspaceLocation(TypedArray timePickerAttrs) {
        final int location = timePickerAttrs.getInt(
                R.styleable.BSP_NumberPadTimePicker_bsp_backspaceLocation, LOCATION_HEADER);
        switch (location) {
            case LOCATION_HEADER:
            case LOCATION_FOOTER:
                return location;
            default:
                return LOCATION_HEADER;
        }
    }

    private static void setBackground(View view, Drawable background) {
        if (Build.VERSION.SDK_INT < 16) {
            view.setBackgroundDrawable(background);
        } else {
            view.setBackground(background);
        }
    }
}
