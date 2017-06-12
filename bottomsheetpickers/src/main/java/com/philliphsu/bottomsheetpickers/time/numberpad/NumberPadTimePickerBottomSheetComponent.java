package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.philliphsu.bottomsheetpickers.R;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.philliphsu.bottomsheetpickers.time.numberpad.BackspaceLocation.LOCATION_FOOTER;
import static com.philliphsu.bottomsheetpickers.time.numberpad.BackspaceLocation.LOCATION_HEADER;
import static com.philliphsu.bottomsheetpickers.time.numberpad.ShowFabPolicy.SHOW_FAB_ALWAYS;
import static com.philliphsu.bottomsheetpickers.time.numberpad.ShowFabPolicy.SHOW_FAB_VALID_TIME;

/**
 * Component that installs {@link NumberPadTimePicker#LAYOUT_BOTTOM_SHEET bottom sheet}
 * functionality to a {@link NumberPadTimePicker}.
 */
final class NumberPadTimePickerBottomSheetComponent extends NumberPadTimePicker.NumberPadTimePickerComponent
        implements BottomSheetNumberPadTimePickerThemer {
    /**
     * Color attributes defined in our {@code Context}'s theme.
     *
     * Used to set the default colors for the FAB in code, rather than in XML because themed colors
     * can only be resolved in code below API 23.
     */
    private static final int[] ATTRS_FAB_COLORS = { R.attr.colorButtonNormal, R.attr.colorAccent };
    /**
     * The states associated with each color resolved from each of the attributes in
     * {@link #ATTRS_FAB_COLORS}.
     */
    private static final int[][] STATES_FAB_COLORS = {{-android.R.attr.state_enabled}, {}};

    /** Duration for all FAB animations. */
    private static final int FAB_ANIM_DURATION = 120;

    private final FloatingActionButton mOkButton;

    // Why aren't we using AnimatorSet? We can't reverse() an AnimatorSet,
    // except beginning in Android O Developer Preview.
    private ValueAnimator mFabBackgroundColorAnimator;
    private ValueAnimator mFabElevationAnimator;
    private ValueAnimator mFabIconTintAnimator;

    private @BackspaceLocation int mBackspaceLocation;
    private @ShowFabPolicy int mShowFabPolicy;
    private boolean mAnimateFabIn;
    private boolean mAnimateFabBackgroundColor;
    private boolean mAnimatingToEnabled;

    NumberPadTimePickerBottomSheetComponent(NumberPadTimePicker timePicker, Context context,
            AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(timePicker, context, attrs, defStyleAttr, defStyleRes);
        mOkButton = (FloatingActionButton) timePicker.findViewById(R.id.bsp_ok_button);
        
        final TypedArray timePickerAttrs = context.obtainStyledAttributes(attrs,
                R.styleable.BSP_NumberPadTimePicker, defStyleAttr, defStyleRes);

        final ColorStateList fabBackgroundColor = retrieveFabBackgroundColor(
                timePickerAttrs, context);
        // If we could not create a default ColorStateList, then just leave the current
        // stateless color as is. If the color is stateless, we ignore the value for
        // animateFabBackgroundColor because there is nothing to animate.
        if (fabBackgroundColor != null) {
            final boolean animateFabBackgroundColor = timePickerAttrs.getBoolean(R.styleable.
                    BSP_NumberPadTimePicker_bsp_animateFabBackgroundColor, true);
            setAnimateFabBackgroundColor(animateFabBackgroundColor, fabBackgroundColor, context);
            mOkButton.setBackgroundTintList(fabBackgroundColor);
        }

        final int fabRippleColor = timePickerAttrs.getColor(R.styleable.
                BSP_NumberPadTimePicker_bsp_fabRippleColor, 0);
        if (fabRippleColor != 0) {
            setFabRippleColor(fabRippleColor);
        }

        mAnimateFabIn = timePickerAttrs.getBoolean(R.styleable.
                BSP_NumberPadTimePicker_bsp_animateFabIn, false);
        mShowFabPolicy = retrieveShowFab(timePickerAttrs);
        setInitialFabVisibility(mAnimateFabIn, mShowFabPolicy);

        mBackspaceLocation = retrieveBackspaceLocation(timePickerAttrs);
        applyBackspaceLocation();

        final ColorStateList fabIconTint = timePickerAttrs.getColorStateList(
                R.styleable.BSP_NumberPadTimePicker_bsp_fabIconTint);
        if (fabIconTint != null) {
            setFabIconTint(fabIconTint);
        }

        timePickerAttrs.recycle();
    }

    @Override
    public BottomSheetNumberPadTimePickerThemer setFabBackgroundColor(ColorStateList fabBackgroundColor) {
        if (mAnimateFabBackgroundColor) {
            mFabBackgroundColorAnimator.setIntValues(extractColors(
                    fabBackgroundColor, STATES_FAB_COLORS));
        }
        mOkButton.setBackgroundTintList(fabBackgroundColor);
        return this;
    }

    @Override
    public BottomSheetNumberPadTimePickerThemer setFabRippleColor(@ColorInt int color) {
        mOkButton.setRippleColor(color);
        return this;
    }

    @Override
    public BottomSheetNumberPadTimePickerThemer setFabIconTint(ColorStateList tint) {
        if (tint != null) {
            int[] colors = extractColors(tint, STATES_FAB_COLORS);
            if (mFabIconTintAnimator != null) {
                mFabIconTintAnimator.setIntValues(colors);
            } else {
                mFabIconTintAnimator = createFabIconTintAnimator(colors);
            }
        }
        DrawableCompat.setTintList(mOkButton.getDrawable(), tint);
        return this;
    }

    @Override
    public BottomSheetNumberPadTimePickerThemer setAnimateFabBackgroundColor(boolean animate) {
        setAnimateFabBackgroundColor(animate, mOkButton.getBackgroundTintList(),
                mOkButton.getContext());
        return this;
    }

    @Override
    public BottomSheetNumberPadTimePickerThemer setShowFabPolicy(@ShowFabPolicy int policy) {
        if (policy != mShowFabPolicy) {
            // We're assuming the dialog has not been shown yet, so prepare the visibility state
            // of the FAB for any animation requirements when the dialog is shown.
            setInitialFabVisibility(mAnimateFabIn, policy);
            mShowFabPolicy = policy;
        }
        return this;
    }

    @Override
    public BottomSheetNumberPadTimePickerThemer setBackspaceLocation(@BackspaceLocation int location) {
        if (location != mBackspaceLocation) {
            mBackspaceLocation = location;
            applyBackspaceLocation();
        }
        return this;
    }

    @Override
    public BottomSheetNumberPadTimePickerThemer setAnimateFabIn(boolean animateIn) {
        if (animateIn != mAnimateFabIn) {
            setInitialFabVisibility(animateIn, mShowFabPolicy);
            mAnimateFabIn = animateIn;
        }
        return this;
    }

    @Override
    View inflate(Context context, NumberPadTimePicker root) {
        return View.inflate(context, R.layout.bsp_bottomsheet_numberpad_time_picker, root);
    }

    FloatingActionButton getOkButton() {
        return mOkButton;
    }

    boolean isAnimateFabIn() {
        return mAnimateFabIn;
    }

    @ShowFabPolicy
    int getShowFabPolicy() {
        return mShowFabPolicy;
    }

    boolean isAnimateFabBackgroundColor() {
        return mAnimateFabBackgroundColor;
    }

    void setOkButtonEnabled(boolean enabled) {
        final boolean enabledDiff = mOkButton.isEnabled() != enabled;
        if (mShowFabPolicy == SHOW_FAB_VALID_TIME) {
            if (enabled) {
                mOkButton.show();
            } else {
                mOkButton.hide(mFabHideListener);
            }
        } else if (mAnimateFabBackgroundColor) {
            if (enabledDiff) {
                // Started animators are not necessarily running. They may have start delays, in
                // which case they have not run yet, or they may have been paused during running.
                // We do not need to be concerned with the latter case, because we don't pause.
                // Therefore, we are filtering out animators that are running or are set to run
                // after some set delay.
                if (!mFabBackgroundColorAnimator.isStarted() && !mFabElevationAnimator.isStarted()) {
                    if (enabled) {
                        // Animate from disabled color to enabled color.
                        mFabBackgroundColorAnimator.start();
                        mFabElevationAnimator.start();
                    } else {
                        // Animate from enabled color to disabled color.
                        mFabBackgroundColorAnimator.reverse();
                        mFabElevationAnimator.reverse();
                    }
                    mAnimatingToEnabled = enabled;
                } else {
                    mFabBackgroundColorAnimator.end();
                    mFabElevationAnimator.end();
                    mOkButton.setEnabled(enabled);
                }
            }
        } else {
            mOkButton.setEnabled(enabled);
        }

        // If the FAB is shown only on valid times, there is no need to animate the tint,
        // just as there was no need to animate the FAB's background color.
        if (mShowFabPolicy == SHOW_FAB_ALWAYS && enabledDiff && mFabIconTintAnimator != null) {
            if (!mFabIconTintAnimator.isStarted()) {
                if (enabled) {
                    mFabIconTintAnimator.start();
                } else {
                    mFabIconTintAnimator.reverse();
                }
            } else {
                mFabIconTintAnimator.end();
            }
        }
    }

    void showOkButton() {
        if (mShowFabPolicy == SHOW_FAB_ALWAYS && mAnimateFabIn) {
            mOkButton.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOkButton.show();
                }
            }, 300);
        }
    }

    /**
     * Sets the initial visibility of the FAB when the dialog is shown, based on the animation
     * requirements imposed on the FAB.
     */
    private void setInitialFabVisibility(boolean animateFabIn, @ShowFabPolicy int showFabPolicy) {
        // For the FAB to actually animate in, it cannot be visible initially.
        mOkButton.setVisibility(animateFabIn || showFabPolicy == SHOW_FAB_VALID_TIME
                ? INVISIBLE : VISIBLE);
    }

    private void setAnimateFabBackgroundColor(boolean animate,
            ColorStateList fabBackgroundColor, Context context) {
        if (animate != mAnimateFabBackgroundColor) {
            if (animate) {
                if (mFabBackgroundColorAnimator == null) {
                    mFabBackgroundColorAnimator = createFabBackgroundColorAnimator(
                            extractColors(fabBackgroundColor, STATES_FAB_COLORS));
                }
                if (mFabElevationAnimator == null) {
                    mFabElevationAnimator = createFabElevationAnimator(context);
                }
            } else {
                mFabBackgroundColorAnimator = null;
                mFabElevationAnimator = null;
            }
            mAnimateFabBackgroundColor = animate;
        }
    }

    private void applyBackspaceLocation() {
        // Make sure we're not posting duplicates.
        mNumberPad.removeCallbacks(mApplyBackspaceLocationRunnable);
        // Wait until drawing has finished before manipulating our view tree.
        // If the backspace key should move to LOCATION_FOOTER, then it goes
        // to the final row of the GridLayout that backs the number pad. However,
        // because drawing may not be finished at this point, and because
        // an explicit row count was not set on the GridLayout, getRowCount()
        // would return the number of rows that have been drawn so far.
        // This caveat does not apply to getColumnCount(), because an explicit
        // column count was set.
        mNumberPad.post(mApplyBackspaceLocationRunnable);
    }

    @NonNull
    private ValueAnimator createFabBackgroundColorAnimator(int[] colors) {
        final ValueAnimator fabBackgroundColorAnimator = newArgbValueAnimator(colors);
        fabBackgroundColorAnimator.setDuration(FAB_ANIM_DURATION);
        fabBackgroundColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOkButton.setBackgroundTintList(ColorStateList.valueOf(
                        (int) animation.getAnimatedValue()));
            }
        });
        // We can add this listener to either animator, since they have the same duration.
        fabBackgroundColorAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mOkButton.setEnabled(mAnimatingToEnabled);
            }
        });

        return fabBackgroundColorAnimator;
    }

    @NonNull
    private ValueAnimator createFabIconTintAnimator(int[] colors) {
        ValueAnimator anim = newArgbValueAnimator(colors);
        anim.setDuration(FAB_ANIM_DURATION);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                DrawableCompat.setTintList(mOkButton.getDrawable(), ColorStateList.valueOf(
                        (int) animation.getAnimatedValue()));
            }
        });
        return anim;
    }

    @NonNull
    private ValueAnimator createFabElevationAnimator(Context context) {
        final float elevation = context.getResources().getDimension(
                R.dimen.bsp_bottom_sheet_grid_picker_fab_elevation);
        final String elevationProperty = Build.VERSION.SDK_INT >= 21 ?
                "elevation" : "compatElevation";
        return ObjectAnimator.ofFloat(mOkButton, elevationProperty, elevation).setDuration(
                FAB_ANIM_DURATION);
    }

    /**
     * Switches the visibility of the FAB from GONE to INVISIBLE when hide() is called.
     */
    private final FloatingActionButton.OnVisibilityChangedListener mFabHideListener =
            new FloatingActionButton.OnVisibilityChangedListener() {
        @Override
        public void onHidden(FloatingActionButton fab) {
            fab.setVisibility(INVISIBLE);
        }
    };

    private final Runnable mApplyBackspaceLocationRunnable = new Runnable() {
        @Override
        public void run() {
            switch (mBackspaceLocation) {
                case LOCATION_HEADER:
                    break;
                case LOCATION_FOOTER:
                    ((ViewGroup) mHeader).removeView(mBackspace);
                    // The row of the cell in which the backspace key should go.
                    // This specifies the row index, which spans one increment,
                    // and indicates the cell should be filled along the row
                    // (horizontal) axis.
                    final GridLayout.Spec rowSpec = GridLayout.spec(
                            mNumberPad.getRowCount() - 1, GridLayout.FILL);
                    // The column of the cell in which the backspace key should go.
                    // This specifies the column index, which spans one increment,
                    // and indicates the cell should be filled along the column
                    // (vertical) axis.
                    final GridLayout.Spec columnSpec = GridLayout.spec(
                            mNumberPad.getColumnCount() - 1, GridLayout.FILL);
                    mNumberPad.addView(mBackspace, new GridLayout.LayoutParams(
                            rowSpec, columnSpec));
                    break;
            }
        }
    };

    @Nullable
    private static ColorStateList retrieveFabBackgroundColor(
            TypedArray timePickerAttrs, Context context) {
        ColorStateList fabBackgroundColor = timePickerAttrs.getColorStateList(
                R.styleable.BSP_NumberPadTimePicker_bsp_fabBackgroundColor);
        if (fabBackgroundColor == null) {
            // Set default ColorStateList.
            // Themed color attributes in a ColorStateList defined in XML cannot be resolved
            // correctly below API 23, so we can only do this in code.
            final int[] colors = resolveColorAttributesFromTheme(context, ATTRS_FAB_COLORS);
            // Check if colors are valid.
            if (isAllNonzero(colors)) {
                fabBackgroundColor = new ColorStateList(STATES_FAB_COLORS, colors);
            }
        }
        return fabBackgroundColor;
    }

    @NonNull
    private static int[] resolveColorAttributesFromTheme(Context context, @StyleableRes int[] attrs) {
        final TypedArray ta = context.obtainStyledAttributes(attrs);
        final int[] colors = new int[attrs.length];
        for (int idxAttr = 0; idxAttr < colors.length; idxAttr++) {
            colors[idxAttr] = ta.getColor(idxAttr, 0);
        }
        ta.recycle();
        return colors;
    }

    @ShowFabPolicy
    private static int retrieveShowFab(TypedArray timePickerAttrs) {
        final int policy = timePickerAttrs.getInt(R.styleable.
                BSP_NumberPadTimePicker_bsp_showFab, SHOW_FAB_ALWAYS);
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
        final int location = timePickerAttrs.getInt(R.styleable.
                BSP_NumberPadTimePicker_bsp_backspaceLocation, LOCATION_HEADER);
        switch (location) {
            case LOCATION_HEADER:
            case LOCATION_FOOTER:
                return location;
            default:
                return LOCATION_HEADER;
        }
    }

    private static boolean isAllNonzero(int[] a) {
        for (int elem : a) {
            if (elem == 0) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    private static int[] extractColors(ColorStateList colorStateList, int[][] states) {
        int[] colors = new int[states.length];
        int idx = 0;
        for (int[] stateSet : states) {
            // The empty state is peculiar in that getColorForState() will not return
            // the default color, but rather any color defined in the ColorStateList
            // for any state.
            // https://developer.android.com/reference/android/content/res/ColorStateList.html
            // "Each item defines a set of state spec and color pairs, where the state
            // spec is a series of attributes set to either true or false to represent
            // inclusion or exclusion. If an attribute is not specified for an item,
            // it may be any value."
            // "An item with no state spec is considered to match any set of states
            // and is generally useful as a final item to be used as a default."
            colors[idx++] = stateSet.length == 0 ? colorStateList.getDefaultColor()
                    : colorStateList.getColorForState(stateSet, 0);
        }
        return colors;
    }

    @NonNull
    private static ValueAnimator newArgbValueAnimator(int[] colors) {
        // Equivalent to ValueAnimator.ofArgb() which is only for API 21+.
        ValueAnimator animator = ValueAnimator.ofInt(colors);
        animator.setEvaluator(new ArgbEvaluator());
        return animator;
    }
}
