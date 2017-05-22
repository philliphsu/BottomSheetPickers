package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.philliphsu.bottomsheetpickers.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.philliphsu.bottomsheetpickers.view.Preconditions.checkNotNull;

/**
 * Component that installs {@link NumberPadTimePicker#LAYOUT_BOTTOM_SHEET bottom sheet}
 * functionality to a {@link NumberPadTimePicker}.
 */
final class NumberPadTimePickerBottomSheetComponent 
        extends NumberPadTimePicker.NumberPadTimePickerComponent {
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
    private static final int FAB_ANIM_DURATION = 80;

    /** Option to place the backspace button in the header. */
    private static final int LOCATION_HEADER = 0;
    /** Option to place the backspace button in the footer. */
    private static final int LOCATION_FOOTER = 1;

    @IntDef({LOCATION_HEADER, LOCATION_FOOTER})
    @Retention(RetentionPolicy.SOURCE)
    private @interface BackspaceLocation {}

    /** Option to always show the FAB. */
    static final int SHOW_FAB_ALWAYS = 0;
    /** Option to only show the FAB when the inputted sequence makes a valid time. */
    static final int SHOW_FAB_VALID_TIME = 1;

    @IntDef({SHOW_FAB_ALWAYS, SHOW_FAB_VALID_TIME})
    @Retention(RetentionPolicy.SOURCE)
    @interface ShowFabPolicy {}

    private final FloatingActionButton mOkButton;
    private final ValueAnimator mFabBackgroundColorAnimator;

    @ShowFabPolicy
    private final int mShowFabPolicy;
    private final boolean mAnimateFabIn;
    private final boolean mAnimateFabBackgroundColor;
    
    private boolean mAnimatingToEnabled;

    NumberPadTimePickerBottomSheetComponent(NumberPadTimePicker timePicker, Context context,
            AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(timePicker, context, attrs, defStyleAttr, defStyleRes);
        mOkButton = (FloatingActionButton) timePicker.findViewById(R.id.bsp_ok_button);
        
        final TypedArray timePickerAttrs = context.obtainStyledAttributes(attrs,
                R.styleable.BSP_NumberPadTimePicker, defStyleAttr, defStyleRes);

        final ColorStateList fabBackgroundColor = retrieveFabBackgroundColor(
                timePickerAttrs, context);
        mAnimateFabBackgroundColor = timePickerAttrs.getBoolean(
                R.styleable.BSP_NumberPadTimePicker_bsp_animateFabBackgroundColor, true);
        ValueAnimator fabBackgroundColorAnimator = null;
        // If we could not create a default ColorStateList, then just leave the current
        // stateless color as is. If the color is stateless, we ignore the value for
        // animateFabBackgroundColor because there is nothing to animate.
        if (fabBackgroundColor != null) {
            if (mAnimateFabBackgroundColor) {
                // Extract the colors from the ColorStateList.
                int[] colors = new int[STATES_FAB_COLORS.length];
                int idx = 0;
                for (int[] stateSet : STATES_FAB_COLORS) {
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
                    colors[idx++] = stateSet.length == 0 ? fabBackgroundColor.getDefaultColor()
                            : fabBackgroundColor.getColorForState(stateSet, 0);
                }
                // Equivalent to ValueAnimator.ofArgb() which is only for API 21+.
                fabBackgroundColorAnimator = ValueAnimator.ofInt(colors);
                fabBackgroundColorAnimator.setEvaluator(new ArgbEvaluator());
                fabBackgroundColorAnimator.setDuration(FAB_ANIM_DURATION);
                fabBackgroundColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mOkButton.setBackgroundTintList(ColorStateList.valueOf(
                                (int) animation.getAnimatedValue()));
                    }
                });
                fabBackgroundColorAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mOkButton.setEnabled(mAnimatingToEnabled);
                    }
                });
            }
            mOkButton.setBackgroundTintList(fabBackgroundColor);
        }
        mFabBackgroundColorAnimator = mAnimateFabBackgroundColor
                ? checkNotNull(fabBackgroundColorAnimator) : null;

        final int fabRippleColor = timePickerAttrs.getColor(
                R.styleable.BSP_NumberPadTimePicker_bsp_fabRippleColor, 0);
        if (fabRippleColor != 0) {
            mOkButton.setRippleColor(fabRippleColor);
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
        final GridLayout numberPad = (GridLayout) timePicker.findViewById(R.id.bsp_numberpad_time_picker_view);
        final View backspace = timePicker.findViewById(R.id.bsp_backspace);
        final ViewGroup headerView = (ViewGroup) timePicker.findViewById(R.id.bsp_header);
        numberPad.post(new Runnable() {
            @Override
            public void run() {
                switch (backspaceLocation) {
                    case LOCATION_HEADER:
                        break;
                    case LOCATION_FOOTER:
                        headerView.removeView(backspace);
                        // The row of the cell in which the backspace key should go.
                        // This specifies the row index, which spans one increment,
                        // and indicates the cell should be filled along the row
                        // (horizontal) axis.
                        final GridLayout.Spec rowSpec = GridLayout.spec(
                                numberPad.getRowCount() - 1, GridLayout.FILL);
                        // The column of the cell in which the backspace key should go.
                        // This specifies the column index, which spans one increment,
                        // and indicates the cell should be filled along the column
                        // (vertical) axis.
                        final GridLayout.Spec columnSpec = GridLayout.spec(
                                numberPad.getColumnCount() - 1, GridLayout.FILL);
                        numberPad.addView(backspace, new GridLayout.LayoutParams(
                                rowSpec, columnSpec));
                        break;
                }
            }
        });
        
        timePickerAttrs.recycle();
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
        if (mShowFabPolicy == SHOW_FAB_VALID_TIME) {
            if (enabled) {
                mOkButton.show();
            } else {
                mOkButton.hide();
            }
        } else if (mAnimateFabBackgroundColor) {
            if (mOkButton.isEnabled() != enabled) {
                if (enabled) {
                    // Animate from disabled color to enabled color.
                    mFabBackgroundColorAnimator.start();
                } else {
                    // Animate from enabled color to disabled color.
                    mFabBackgroundColorAnimator.reverse();
                }
            }
            mAnimatingToEnabled = enabled;
        } else {
            mOkButton.setEnabled(enabled);
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

    private static boolean isAllNonzero(int[] a) {
        for (int elem : a) {
            if (elem == 0) {
                return false;
            }
        }
        return true;
    }
}
